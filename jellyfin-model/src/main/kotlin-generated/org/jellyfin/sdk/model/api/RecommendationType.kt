// !!        WARNING
// !! DO NOT EDIT THIS FILE
//
// This file is generated by the openapi-generator module and is not meant for manual changes.
// Please read the README.md file in the openapi-generator module for additional information.
package org.jellyfin.sdk.model.api

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class RecommendationType(
	public val serialName: String
) {
	@SerialName("SimilarToRecentlyPlayed")
	SIMILAR_TO_RECENTLY_PLAYED("SimilarToRecentlyPlayed"),
	@SerialName("SimilarToLikedItem")
	SIMILAR_TO_LIKED_ITEM("SimilarToLikedItem"),
	@SerialName("HasDirectorFromRecentlyPlayed")
	HAS_DIRECTOR_FROM_RECENTLY_PLAYED("HasDirectorFromRecentlyPlayed"),
	@SerialName("HasActorFromRecentlyPlayed")
	HAS_ACTOR_FROM_RECENTLY_PLAYED("HasActorFromRecentlyPlayed"),
	@SerialName("HasLikedDirector")
	HAS_LIKED_DIRECTOR("HasLikedDirector"),
	@SerialName("HasLikedActor")
	HAS_LIKED_ACTOR("HasLikedActor"),
	;

	public override fun toString(): String = serialName
}
