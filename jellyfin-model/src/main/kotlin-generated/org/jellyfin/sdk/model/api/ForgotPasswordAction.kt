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
public enum class ForgotPasswordAction(
	public val serialName: String
) {
	@SerialName("ContactAdmin")
	CONTACT_ADMIN("ContactAdmin"),
	@SerialName("PinCode")
	PIN_CODE("PinCode"),
	@SerialName("InNetworkRequired")
	IN_NETWORK_REQUIRED("InNetworkRequired"),
	;

	public override fun toString(): String = serialName
}
