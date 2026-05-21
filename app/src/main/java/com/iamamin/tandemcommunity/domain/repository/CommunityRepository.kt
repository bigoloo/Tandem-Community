package com.iamamin.tandemcommunity.domain.repository

import androidx.paging.PagingData
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getCommunityMembers(): Flow<PagingData<UserDto>>
    fun observeLikedUserIds(): Flow<Set<Long>>
    suspend fun toggleLike(userId: Long)
}