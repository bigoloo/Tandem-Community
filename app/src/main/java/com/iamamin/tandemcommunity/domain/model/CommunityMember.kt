package com.iamamin.tandemcommunity.domain.model

data class CommunityMember(
    val id: String,
    val firstname: String,
    val topic: String,
    val pictureUrl: String,
    val natives: List<String>,
    val learns: List<String>,
    val referenceCnt: Int,
    val isNew: Boolean,
    val isLiked: Boolean
)