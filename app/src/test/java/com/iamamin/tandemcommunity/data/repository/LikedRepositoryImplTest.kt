package com.iamamin.tandemcommunity.data.repository

import com.iamamin.tandemcommunity.data.local.dao.LikedUserDao
import com.iamamin.tandemcommunity.data.local.entity.LikedUserEntity
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LikedRepositoryImplTest {

    private val dao = mockk<LikedUserDao>()
    private lateinit var repository: LikedRepositoryImpl

    @Before
    fun setUp() {
        repository = LikedRepositoryImpl(dao)
    }

    // region observeLikedUserIds()

    @Test
    fun `observeLikedUserIds emits empty set when dao returns empty list`() = runTest {
        every { dao.observeAll() } returns flowOf(emptyList())

        val result = repository.observeLikedUserIds().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `observeLikedUserIds emits set of liked user ids`() = runTest {
        every { dao.observeAll() } returns flowOf(listOf(1L, 2L, 3L))

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf(1L, 2L, 3L), result)
    }

    @Test
    fun `observeLikedUserIds converts list to set removing duplicates`() = runTest {
        every { dao.observeAll() } returns flowOf(listOf(1L, 1L, 2L))

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf(1L, 2L), result)
    }

    @Test
    fun `observeLikedUserIds reflects new emissions from the dao flow`() = runTest {
        val daoFlow = MutableStateFlow<List<Long>>(emptyList())
        every { dao.observeAll() } returns daoFlow

        assertTrue(repository.observeLikedUserIds().first().isEmpty())

        daoFlow.value = listOf(1L, 2L)
        assertEquals(setOf(1L, 2L), repository.observeLikedUserIds().first())
    }

    // endregion

    // region toggleLike()

    @Test
    fun `toggleLike calls like when user is not yet liked`() = runTest {
        coEvery { dao.isLiked(1L) } returns false
        coEvery { dao.like(any()) } just Runs

        repository.toggleLike(1L)

        coVerify { dao.like(LikedUserEntity(1L)) }
    }

    @Test
    fun `toggleLike does not call unlike when user is not yet liked`() = runTest {
        coEvery { dao.isLiked(1L) } returns false
        coEvery { dao.like(any()) } just Runs

        repository.toggleLike(1L)

        coVerify(exactly = 0) { dao.unlike(any()) }
    }

    @Test
    fun `toggleLike calls unlike when user is already liked`() = runTest {
        coEvery { dao.isLiked(1L) } returns true
        coEvery { dao.unlike(any()) } just Runs

        repository.toggleLike(1L)

        coVerify { dao.unlike(1L) }
    }

    @Test
    fun `toggleLike does not call like when user is already liked`() = runTest {
        coEvery { dao.isLiked(1L) } returns true
        coEvery { dao.unlike(any()) } just Runs

        repository.toggleLike(1L)

        coVerify(exactly = 0) { dao.like(any()) }
    }

    // endregion
}
