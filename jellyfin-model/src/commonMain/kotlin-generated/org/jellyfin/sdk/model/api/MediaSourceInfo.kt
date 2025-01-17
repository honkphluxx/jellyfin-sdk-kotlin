// !!        WARNING
// !! DO NOT EDIT THIS FILE
//
// This file is generated by the openapi-generator module and is not meant for manual changes.
// Please read the README.md file in the openapi-generator module for additional information.
package org.jellyfin.sdk.model.api

import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class MediaSourceInfo(
	@SerialName("Protocol")
	public val protocol: MediaProtocol,
	@SerialName("Id")
	public val id: String? = null,
	@SerialName("Path")
	public val path: String? = null,
	@SerialName("EncoderPath")
	public val encoderPath: String? = null,
	@SerialName("EncoderProtocol")
	public val encoderProtocol: MediaProtocol? = null,
	@SerialName("Type")
	public val type: MediaSourceType,
	@SerialName("Container")
	public val container: String? = null,
	@SerialName("Size")
	public val size: Long? = null,
	@SerialName("Name")
	public val name: String? = null,
	/**
	 * Gets or sets a value indicating whether the media is remote.
	 * Differentiate internet url vs local network.
	 */
	@SerialName("IsRemote")
	public val isRemote: Boolean,
	@SerialName("ETag")
	public val eTag: String? = null,
	@SerialName("RunTimeTicks")
	public val runTimeTicks: Long? = null,
	@SerialName("ReadAtNativeFramerate")
	public val readAtNativeFramerate: Boolean,
	@SerialName("IgnoreDts")
	public val ignoreDts: Boolean,
	@SerialName("IgnoreIndex")
	public val ignoreIndex: Boolean,
	@SerialName("GenPtsInput")
	public val genPtsInput: Boolean,
	@SerialName("SupportsTranscoding")
	public val supportsTranscoding: Boolean,
	@SerialName("SupportsDirectStream")
	public val supportsDirectStream: Boolean,
	@SerialName("SupportsDirectPlay")
	public val supportsDirectPlay: Boolean,
	@SerialName("IsInfiniteStream")
	public val isInfiniteStream: Boolean,
	@SerialName("RequiresOpening")
	public val requiresOpening: Boolean,
	@SerialName("OpenToken")
	public val openToken: String? = null,
	@SerialName("RequiresClosing")
	public val requiresClosing: Boolean,
	@SerialName("LiveStreamId")
	public val liveStreamId: String? = null,
	@SerialName("BufferMs")
	public val bufferMs: Int? = null,
	@SerialName("RequiresLooping")
	public val requiresLooping: Boolean,
	@SerialName("SupportsProbing")
	public val supportsProbing: Boolean,
	@SerialName("VideoType")
	public val videoType: VideoType? = null,
	@SerialName("IsoType")
	public val isoType: IsoType? = null,
	@SerialName("Video3DFormat")
	public val video3dFormat: Video3dFormat? = null,
	@SerialName("MediaStreams")
	public val mediaStreams: List<MediaStream>? = null,
	@SerialName("MediaAttachments")
	public val mediaAttachments: List<MediaAttachment>? = null,
	@SerialName("Formats")
	public val formats: List<String>? = null,
	@SerialName("Bitrate")
	public val bitrate: Int? = null,
	@SerialName("Timestamp")
	public val timestamp: TransportStreamTimestamp? = null,
	@SerialName("RequiredHttpHeaders")
	public val requiredHttpHeaders: Map<String, String?>? = null,
	@SerialName("TranscodingUrl")
	public val transcodingUrl: String? = null,
	@SerialName("TranscodingSubProtocol")
	public val transcodingSubProtocol: String? = null,
	@SerialName("TranscodingContainer")
	public val transcodingContainer: String? = null,
	@SerialName("AnalyzeDurationMs")
	public val analyzeDurationMs: Int? = null,
	@SerialName("DefaultAudioStreamIndex")
	public val defaultAudioStreamIndex: Int? = null,
	@SerialName("DefaultSubtitleStreamIndex")
	public val defaultSubtitleStreamIndex: Int? = null,
)
