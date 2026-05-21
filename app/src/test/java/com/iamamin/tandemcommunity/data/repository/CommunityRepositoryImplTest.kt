package com.iamamin.tandemcommunity.data.repository

import com.iamamin.tandemcommunity.data.local.dao.LikedUserDao
import com.iamamin.tandemcommunity.data.local.entity.LikedUserEntity
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.data.remote.model.CommunityResponse
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommunityRepositoryImplTest {

    private lateinit var fakeLikedUserDao: FakeLikedUserDao
    private lateinit var fakeCommunityApi: FakeCommunityApi
    private lateinit var repository: CommunityRepositoryImpl

    @Before
    fun setUp() {
        fakeLikedUserDao = FakeLikedUserDao()
        fakeCommunityApi = FakeCommunityApi()
        repository = CommunityRepositoryImpl(fakeCommunityApi, fakeLikedUserDao)
    }

    @Test
    fun `getCommunityMembers returns a non-null flow`() {
        assertNotNull(repository.getCommunityUsers())
    }

    @Test
    fun `observeLikedUserIds emits empty set when no users are liked`() = runBlocking {
        val result = repository.observeLikedUserIds().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `observeLikedUserIds emits set containing pre-liked user`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity(1))

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf(1), result)
    }

    @Test
    fun `observeLikedUserIds emits set containing all pre-liked users`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity(1))
        fakeLikedUserDao.like(LikedUserEntity(2))

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf(1, 2), result)
    }

    @Test
    fun `toggleLike likes a user who is not yet liked`() = runBlocking {
        repository.toggleLike(1)

        assertTrue(fakeLikedUserDao.isLiked(1))
    }

    @Test
    fun `toggleLike unlikes a user who is already liked`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity(1))

        repository.toggleLike(1)

        assertFalse(fakeLikedUserDao.isLiked(1))
    }

    @Test
    fun `toggleLike does not affect other liked users when unliking`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity(1))
        fakeLikedUserDao.like(LikedUserEntity(2))

        repository.toggleLike(1)

        assertFalse(fakeLikedUserDao.isLiked(1))
        assertTrue(fakeLikedUserDao.isLiked(2))
    }

    @Test
    fun `toggleLike re-likes a user after being unliked`() = runBlocking {
        repository.toggleLike(1) // like
        repository.toggleLike(1) // unlike
        repository.toggleLike(1) // like again

        assertTrue(fakeLikedUserDao.isLiked(1))
    }

    @Test
    fun `observeLikedUserIds reflects state after toggleLike`() = runBlocking {
        repository.toggleLike(1)
        repository.toggleLike(2)

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf(1, 2), result)
    }

    @Test
    fun `observeLikedUserIds reflects state after unlike via toggleLike`() = runBlocking {
        repository.toggleLike(1)
        repository.toggleLike(2)
        repository.toggleLike(1)

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf(2), result)
    }
}

private class FakeLikedUserDao : LikedUserDao {

    private val likedUsers = MutableStateFlow<List<Long>>(emptyList())

    override fun observeAll(): Flow<List<Long>> = likedUsers

    override suspend fun like(entity: LikedUserEntity) {
        if (!likedUsers.value.contains(entity.userId)) {
            likedUsers.value += entity.userId
        }
    }

    override suspend fun unlike(userId: Long) {
        likedUsers.value = likedUsers.value.filter { it != userId }
    }

    override suspend fun isLiked(userId: Long): Boolean = likedUsers.value.contains(userId)
}

private class FakeCommunityApi : CommunityApi {

    var usersToReturn: List<UserDto> = emptyList()

    override suspend fun getCommunity(pageNumber: Int): CommunityResponse {
        return CommunityResponse(
            response = usersToReturn,
            errorCode = null,
            type = "success"
        )
    }
}
