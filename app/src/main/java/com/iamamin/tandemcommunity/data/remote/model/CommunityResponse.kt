package com.iamamin.tandemcommunity.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityResponse(
    @SerialName("response") val response: List<UserDto>,
    @SerialName("errorCode") val errorCode: Int?,
    @SerialName("type") val type: String
)