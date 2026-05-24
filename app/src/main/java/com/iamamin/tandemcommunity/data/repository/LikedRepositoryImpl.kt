package com.iamamin.tandemcommunity.data.repository

import com.iamamin.tandemcommunity.data.local.dao.LikedUserDao
import com.iamamin.tandemcommunity.data.local.entity.LikedUserEntity
import com.iamamin.tandemcommunity.domain.repository.LikedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LikedRepositoryImpl(
    private val likedUserDao: LikedUserDao
): LikedRepository {
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
