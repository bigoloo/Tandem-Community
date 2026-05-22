package com.iamamin.tandemcommunity.domain.model

data class CommunityMember(
    val id: Long,
    val firstname: String,
    val topic: String,
    val pictureUrl: String,
    val native: String,
    val learn: String,
    val isNew: Boolean,
    val isLiked: Boolean
)