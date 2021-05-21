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
public enum class RecordingStatus(
	public val serialName: String
) {
	@SerialName("New")
	NEW("New"),
	@SerialName("InProgress")
	IN_PROGRESS("InProgress"),
	@SerialName("Completed")
	COMPLETED("Completed"),
	@SerialName("Cancelled")
	CANCELLED("Cancelled"),
	@SerialName("ConflictedOk")
	CONFLICTED_OK("ConflictedOk"),
	@SerialName("ConflictedNotOk")
	CONFLICTED_NOT_OK("ConflictedNotOk"),
	@SerialName("Error")
	ERROR("Error"),
	;

	public override fun toString(): String = serialName
}
