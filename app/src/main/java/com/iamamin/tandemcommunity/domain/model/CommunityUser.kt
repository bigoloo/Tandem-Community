package com.iamamin.tandemcommunity.domain.model

data class CommunityUser(
    val id: Long,
    val firstname: String,
    val topic: String,
    val pictureUrl: String,
    val natives: List<String>,
    val learns: List<String>,
    val referenceCnt: Int
)
