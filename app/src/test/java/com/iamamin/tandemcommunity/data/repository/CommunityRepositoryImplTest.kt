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
        assertNotNull(repository.getCommunityMembers())
    }

    @Test
    fun `observeLikedUserIds emits empty set when no users are liked`() = runBlocking {
        val result = repository.observeLikedUserIds().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `observeLikedUserIds emits set containing pre-liked user`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity("user1"))

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf("user1"), result)
    }

    @Test
    fun `observeLikedUserIds emits set containing all pre-liked users`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity("user1"))
        fakeLikedUserDao.like(LikedUserEntity("user2"))

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf("user1", "user2"), result)
    }

    @Test
    fun `toggleLike likes a user who is not yet liked`() = runBlocking {
        repository.toggleLike("user1")

        assertTrue(fakeLikedUserDao.isLiked("user1"))
    }

    @Test
    fun `toggleLike unlikes a user who is already liked`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity("user1"))

        repository.toggleLike("user1")

        assertFalse(fakeLikedUserDao.isLiked("user1"))
    }

    @Test
    fun `toggleLike does not affect other liked users when unliking`() = runBlocking {
        fakeLikedUserDao.like(LikedUserEntity("user1"))
        fakeLikedUserDao.like(LikedUserEntity("user2"))

        repository.toggleLike("user1")

        assertFalse(fakeLikedUserDao.isLiked("user1"))
        assertTrue(fakeLikedUserDao.isLiked("user2"))
    }

    @Test
    fun `toggleLike re-likes a user after being unliked`() = runBlocking {
        repository.toggleLike("user1") // like
        repository.toggleLike("user1") // unlike
        repository.toggleLike("user1") // like again

        assertTrue(fakeLikedUserDao.isLiked("user1"))
    }

    @Test
    fun `observeLikedUserIds reflects state after toggleLike`() = runBlocking {
        repository.toggleLike("user1")
        repository.toggleLike("user2")

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf("user1", "user2"), result)
    }

    @Test
    fun `observeLikedUserIds reflects state after unlike via toggleLike`() = runBlocking {
        repository.toggleLike("user1")
        repository.toggleLike("user2")
        repository.toggleLike("user1")

        val result = repository.observeLikedUserIds().first()

        assertEquals(setOf("user2"), result)
    }
}

private class FakeLikedUserDao : LikedUserDao {

    private val likedUsers = MutableStateFlow<List<String>>(emptyList())

    override fun observeAll(): Flow<List<String>> = likedUsers

    override suspend fun like(entity: LikedUserEntity) {
        if (!likedUsers.value.contains(entity.userId)) {
            likedUsers.value = likedUsers.value + entity.userId
        }
    }

    override suspend fun unlike(userId: String) {
        likedUsers.value = likedUsers.value.filter { it != userId }
    }

    override suspend fun isLiked(userId: String): Boolean = likedUsers.value.contains(userId)
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
