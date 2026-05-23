package com.iamamin.tandemcommunity.data.repository

import androidx.paging.testing.asSnapshot
import com.iamamin.tandemcommunity.data.paging.CommunityPagingSource
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.data.remote.model.CommunityResponse
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.domain.model.CommunityUser
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommunityRepositoryImplTest {

    private val api = mockk<CommunityApi>()
    private lateinit var repository: CommunityRepositoryImpl

    @Before
    fun setUp() {
        coEvery { api.getCommunity(any()) } returns fakeResponse()
        repository = CommunityRepositoryImpl(CommunityPagingSource(api))
    }

    @Test
    fun `getCommunityUsers returns a non-null flow`() {
        assertNotNull(repository.getCommunityUsers())
    }

    @Test
    fun `getCommunityUsers emits items returned by the paging source`() = runTest {
        coEvery { api.getCommunity(any()) } returns fakeResponse(listOf(fakeUserDto(1), fakeUserDto(2)))

        val items = repository.getCommunityUsers().asSnapshot()

        assertEquals(listOf(fakeCommunityUser(1), fakeCommunityUser(2)), items)
    }

    @Test
    fun `getCommunityUsers emits empty list when paging source returns no data`() = runTest {
        coEvery { api.getCommunity(any()) } returns fakeResponse(emptyList())

        val items = repository.getCommunityUsers().asSnapshot()

        assertTrue(items.isEmpty())
    }

    @Test
    fun `getCommunityUsers emits all items across multiple pages`() = runTest {
        val page1 = List(20) { fakeUserDto(it.toLong()) }
        val page2 = listOf(fakeUserDto(20))
        coEvery { api.getCommunity(1) } returns fakeResponse(page1)
        coEvery { api.getCommunity(2) } returns fakeResponse(page2)

        val items = repository.getCommunityUsers().asSnapshot {
            scrollTo(index = 20)
        }

        assertEquals(21, items.size)
        assertEquals(fakeCommunityUser(20), items.last())
    }

    // region helpers

    private fun fakeResponse(users: List<UserDto> = emptyList()) = CommunityResponse(
        response = users, errorCode = null, type = "success"
    )

    private fun fakeUserDto(id: Long) = UserDto(
        id = id,
        topic = "topic_$id",
        firstname = "User $id",
        pictureUrl = "https://example.com/$id.jpg",
        natives = listOf("en"),
        learns = listOf("de"),
        referenceCnt = 0
    )

    private fun fakeCommunityUser(id: Long) = CommunityUser(
        id = id,
        topic = "topic_$id",
        firstname = "User $id",
        pictureUrl = "https://example.com/$id.jpg",
        natives = listOf("en"),
        learns = listOf("de"),
        referenceCnt = 0
    )

    // endregion
}
