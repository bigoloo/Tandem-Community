package com.iamamin.tandemcommunity.domain.usecase

import com.iamamin.tandemcommunity.domain.repository.LikedRepository

class ToggleLikeUseCase(
    private val likedRepository: LikedRepository
) {
    suspend operator fun invoke(userId: Long) {
        likedRepository.toggleLike(userId)
    }
}
