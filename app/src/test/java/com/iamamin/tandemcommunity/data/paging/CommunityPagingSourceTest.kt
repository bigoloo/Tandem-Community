package com.iamamin.tandemcommunity.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.data.remote.model.CommunityResponse
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommunityPagingSourceTest {

    private lateinit var fakeApi: FakeCommunityApi
    private lateinit var pagingSource: CommunityPagingSource

    @Before
    fun setUp() {
        fakeApi = FakeCommunityApi()
        pagingSource = CommunityPagingSource(fakeApi)
    }

    // region load()

    @Test
    fun `load returns Page with users returned by the api`() = runBlocking {
        fakeApi.usersToReturn = listOf(fakeUser(1), fakeUser(1))

        val result = pagingSource.load(refreshParams(key = 1))

        assertTrue(result is PagingSource.LoadResult.Page)
        assertEquals(
            listOf(fakeUser(1), fakeUser(1)), (result as PagingSource.LoadResult.Page).data
        )
    }

    @Test
    fun `load uses page 1 when key is null`() = runBlocking {
        pagingSource.load(refreshParams(key = null))

        assertEquals(1, fakeApi.lastRequestedPage)
    }

    @Test
    fun `load passes provided page number to api`() = runBlocking {
        pagingSource.load(refreshParams(key = 3))

        assertEquals(3, fakeApi.lastRequestedPage)
    }

    @Test
    fun `load returns null prevKey on first page`() = runBlocking {
        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertNull(result.prevKey)
    }

    @Test
    fun `load returns correct prevKey on subsequent pages`() = runBlocking {
        val result = pagingSource.load(refreshParams(key = 3)) as PagingSource.LoadResult.Page

        assertEquals(2, result.prevKey)
    }

    @Test
    fun `load returns null nextKey when response has fewer than 20 items`() = runBlocking {
        fakeApi.usersToReturn = List(19) { fakeUser(it.toLong()) }

        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `load returns null nextKey when response is empty`() = runBlocking {
        fakeApi.usersToReturn = emptyList()

        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `load returns incremented nextKey when response has exactly 20 items`() = runBlocking {
        fakeApi.usersToReturn = List(20) { fakeUser(it.toLong()) }

        val result = pagingSource.load(refreshParams(key = 2)) as PagingSource.LoadResult.Page

        assertEquals(3, result.nextKey)
    }

    @Test
    fun `load returns Error when api throws`() = runBlocking {
        fakeApi.shouldThrow = true

        val result = pagingSource.load(refreshParams(key = 1))

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    // endregion

    // region getRefreshKey()

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() {
        val state = PagingState<Int, UserDto>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        assertNull(pagingSource.getRefreshKey(state))
    }

    @Test
    fun `getRefreshKey returns prevKey plus 1 when closest page has prevKey`() {
        val page = PagingSource.LoadResult.Page(
            data = listOf(fakeUser(1)), prevKey = 2, nextKey = 4
        )
        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        assertEquals(3, pagingSource.getRefreshKey(state))
    }

    @Test
    fun `getRefreshKey returns nextKey minus 1 when closest page has no prevKey`() {
        val page = PagingSource.LoadResult.Page(
            data = listOf(fakeUser(1)), prevKey = null, nextKey = 2
        )
        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        assertEquals(1, pagingSource.getRefreshKey(state))
    }
    // endregion

    // region helpers

    private fun refreshParams(key: Int?) = PagingSource.LoadParams.Refresh(
        key = key, loadSize = 20, placeholdersEnabled = false
    )

    private fun fakeUser(id: Long) = UserDto(
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

private class FakeCommunityApi : CommunityApi {
    var usersToReturn: List<UserDto> = emptyList()
    var shouldThrow: Boolean = false
    var exceptionToThrow: Exception = RuntimeException("api error")
    var lastRequestedPage: Int = -1

    override suspend fun getCommunity(pageNumber: Int): CommunityResponse {
        lastRequestedPage = pageNumber
        if (shouldThrow) throw exceptionToThrow
        return CommunityResponse(response = usersToReturn, errorCode = null, type = "success")
    }
}
