package com.iamamin.tandemcommunity.domain.usecase

import com.iamamin.tandemcommunity.domain.repository.LikedRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleLikeUseCaseTest {

    private val likedRepository = mockk<LikedRepository>(relaxed = true)
    private val useCase = ToggleLikeUseCase(likedRepository)

    @Test
    fun `invoke calls toggleLike on repository with the given userId`() = runTest {
        useCase(42L)

        coVerify { likedRepository.toggleLike(42L) }
    }
}
