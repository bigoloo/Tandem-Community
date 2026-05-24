package com.iamamin.tandemcommunity.domain.repository

import kotlinx.coroutines.flow.Flow

interface LikedRepository {
    fun observeLikedUserIds(): Flow<Set<Long>>
    suspend fun toggleLike(userId: Long)
}
