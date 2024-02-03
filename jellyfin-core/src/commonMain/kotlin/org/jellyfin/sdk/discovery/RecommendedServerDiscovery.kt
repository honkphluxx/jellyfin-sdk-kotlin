package org.jellyfin.sdk.discovery

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.HttpClientOptions
import org.jellyfin.sdk.api.client.Response
import org.jellyfin.sdk.api.client.exception.ApiClientException
import org.jellyfin.sdk.api.client.exception.InvalidContentException
import org.jellyfin.sdk.api.client.exception.InvalidStatusException
import org.jellyfin.sdk.api.client.exception.SecureConnectionException
import org.jellyfin.sdk.api.client.exception.TimeoutException
import org.jellyfin.sdk.api.client.extensions.systemApi
import org.jellyfin.sdk.model.ServerVersion
import org.jellyfin.sdk.model.api.PublicSystemInfo
import org.jellyfin.sdk.util.currentTimeMillis
import java.security.PrivateKey
import java.security.cert.X509Certificate
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

public class RecommendedServerDiscovery constructor(
	private val jellyfin: Jellyfin,
) {
	private companion object {
		private const val HTTP_OK = 200
		private const val PRODUCT_NAME = "Jellyfin Server"
		private const val SLOW_TIME_THRESHOLD = 1_500
		private val HTTP_TIMEOUT = 3.5.seconds
		private const val MAX_SIMULTANEOUS_RETRIEVALS = 3
	}

	private data class SystemInfoResult(
		val address: String,
		val systemInfo: Result<PublicSystemInfo>,
		val responseTime: Long,
	)

	@Suppress("MagicNumber")
	private fun assignScore(result: SystemInfoResult): RecommendedServerInfo {
		val systemInfo = result.systemInfo.getOrNull()
		val version = systemInfo?.version?.let(ServerVersion::fromString)
		val issues = mutableSetOf<RecommendedServerIssue>()
		val scores = mutableSetOf<RecommendedServerInfoScore>()

		// Failure checks
		result.systemInfo.exceptionOrNull()?.let { exception ->
			when (exception) {
				is SecureConnectionException -> issues.add(RecommendedServerIssue.SecureConnectionFailed(exception))
				is TimeoutException -> issues.add(RecommendedServerIssue.ServerUnreachable(exception))
				// Did not reply with a system information
				else -> issues.add(RecommendedServerIssue.MissingSystemInfo(result.systemInfo.exceptionOrNull()))
			}

			scores.add(RecommendedServerInfoScore.BAD)
		}

		// System Info data validation
		when {
			// Wrong product name - might be a different service on this connection
			systemInfo != null && !systemInfo.productName.equals(PRODUCT_NAME) -> {
				issues.add(RecommendedServerIssue.InvalidProductName(systemInfo.productName))
				scores.add(RecommendedServerInfoScore.BAD)
			}
		}

		// Version checks
		when {
			// Version could not be parsed
			version == null -> {
				issues.add(RecommendedServerIssue.MissingVersion)
				scores.add(RecommendedServerInfoScore.BAD)
			}
			// Server might be incompatible because it's below the client specified minimum supported server version
			version < jellyfin.options.minimumServerVersion -> {
				issues.add(RecommendedServerIssue.UnsupportedServerVersion(version))
				scores.add(RecommendedServerInfoScore.OK)
			}
			// Server might be incompatible because it's below the SDK specified minimum supported server version
			version < Jellyfin.minimumVersion -> {
				issues.add(RecommendedServerIssue.OutdatedServerVersion(version))
				scores.add(RecommendedServerInfoScore.GOOD)
			}
			// API might differ slightly but at least above the minimum version
			version < Jellyfin.apiVersion -> {
				issues.add(RecommendedServerIssue.OutdatedServerVersion(version))
				scores.add(RecommendedServerInfoScore.GOOD)
			}
		}

		// Other checks
		when {
			// Server is not too fast but should work
			result.responseTime > SLOW_TIME_THRESHOLD -> {
				issues.add(RecommendedServerIssue.SlowResponse(result.responseTime))
				scores.add(RecommendedServerInfoScore.GOOD)
			}
		}

		// Calculate score, pick the lowest from the collection or use GREAT when no scores (and issues) added
		val score = scores.minByOrNull { it.score } ?: RecommendedServerInfoScore.GREAT
		// Return results
		return RecommendedServerInfo(
			result.address,
			result.responseTime,
			score,
			issues,
			result.systemInfo,
		)
	}

	private suspend fun getSystemInfoResult(address: String, clientKey: PrivateKey? = null, clientCert: Array<X509Certificate>? = null): SystemInfoResult {
		logger.info { "Requesting public system info for $address" }

		val client = jellyfin.createApi(
			baseUrl = address,
			clientKey = clientKey,
			clientCertChain = clientCert,
			httpClientOptions = HttpClientOptions(
				followRedirects = false,
				connectTimeout = HTTP_TIMEOUT,
				requestTimeout = HTTP_TIMEOUT,
				socketTimeout = HTTP_TIMEOUT
			),
		)

		val responseTimeStart = currentTimeMillis()

		val info: Result<Response<PublicSystemInfo>> = try {
			val response = client.systemApi.getPublicSystemInfo()
			if (response.status == HTTP_OK) Result.success(response)
			else Result.failure(InvalidStatusException(response.status))
		} catch (err: TimeoutException) {
			logger.debug(err) { "Could not connect to $address" }
			Result.failure(err)
		} catch (err: InvalidStatusException) {
			logger.debug(err) { "Received unexpected status ${err.status} from $address" }
			Result.failure(err)
		} catch (err: InvalidContentException) {
			logger.debug(err) { "Could not parse response from $address" }
			Result.failure(err)
		} catch (err: ApiClientException) {
			logger.debug(err) { "Unable to get response from $address" }
			Result.failure(err)
		}
		val responseTime = currentTimeMillis() - responseTimeStart

		return SystemInfoResult(
			address = address,
			systemInfo = info.map(Response<PublicSystemInfo>::content),
			responseTime = responseTime,
		)
	}

	/**
	 * Discover all servers in the [servers] flow and retrieve the public system information to assign a score.
	 * Returned servers are not ordered by score. Use [minimumScore] to automatically remove bad matches.
	 */
	public suspend fun discover(
		servers: Collection<String>,
		minimumScore: RecommendedServerInfoScore,
		clientKey: PrivateKey? = null,
		clientCert: Array<X509Certificate>? = null
	): Collection<RecommendedServerInfo> = withContext(Dispatchers.IO) {
		val semaphore = Semaphore(MAX_SIMULTANEOUS_RETRIEVALS)

		servers
			.map { address ->
				async {
					semaphore.withPermit {
						getSystemInfoResult(address, clientKey, clientCert).let(::assignScore)
					}
				}
			}
			.awaitAll()
			.filter { serverInfo ->
				// Use [minimumScore] to filter out bad score matches
				serverInfo.score.score >= minimumScore.score
			}
	}
}
