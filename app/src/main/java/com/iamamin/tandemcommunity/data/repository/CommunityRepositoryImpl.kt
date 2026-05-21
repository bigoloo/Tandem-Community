package com.iamamin.tandemcommunity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.iamamin.tandemcommunity.data.local.dao.LikedUserDao
import com.iamamin.tandemcommunity.data.local.entity.LikedUserEntity
import com.iamamin.tandemcommunity.data.paging.CommunityPagingSource
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CommunityRepositoryImpl(
    private val api: CommunityApi,
    private val likedUserDao: LikedUserDao
) : CommunityRepository {

    override fun getCommunityMembers(): Flow<PagingData<UserDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommunityPagingSource(api) }
        ).flow
    }

    override fun observeLikedUserIds(): Flow<Set<Long>> {
        return likedUserDao.observeAll().map { it.toSet() }
    }

    override suspend fun toggleLike(userId: Long) {
        if (likedUserDao.isLiked(userId)) {
            likedUserDao.unlike(userId)
        } else {
            likedUserDao.like(LikedUserEntity(userId))
        }
    }
}