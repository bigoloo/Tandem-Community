package com.iamamin.tandemcommunity.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id") val id: Long,
    @SerialName("topic") val topic: String,
    @SerialName("firstName") val firstname: String,
    @SerialName("pictureUrl") val pictureUrl: String,
    @SerialName("natives") val natives: List<String>,
    @SerialName("learns") val learns: List<String>,
    @SerialName("referenceCnt") val referenceCnt: Int
)
