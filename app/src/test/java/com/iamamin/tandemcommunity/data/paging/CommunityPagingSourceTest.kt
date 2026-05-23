package com.iamamin.tandemcommunity.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.data.remote.model.CommunityResponse
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.domain.model.CommunityUser
import com.iamamin.tandemcommunity.domain.model.error.CommunityError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class CommunityPagingSourceTest {

    private val api = mockk<CommunityApi>()
    private lateinit var pagingSource: CommunityPagingSource

    @Before
    fun setUp() {
        coEvery { api.getCommunity(any()) } returns fakeResponse()
        pagingSource = CommunityPagingSource(api)
    }

    // region load()

    @Test
    fun `load returns Page with mapped users`() = runBlocking {
        coEvery { api.getCommunity(any()) } returns fakeResponse(listOf(fakeUserDto(1), fakeUserDto(2)))

        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertEquals(listOf(fakeCommunityUser(1), fakeCommunityUser(2)), result.data)
    }

    @Test
    fun `load uses page 1 when key is null`() = runBlocking {
        pagingSource.load(refreshParams(key = null))

        coVerify { api.getCommunity(1) }
    }

    @Test
    fun `load passes provided page number to api`() = runBlocking {
        pagingSource.load(refreshParams(key = 3))

        coVerify { api.getCommunity(3) }
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
        coEvery { api.getCommunity(any()) } returns fakeResponse(List(19) { fakeUserDto(it.toLong()) })

        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `load returns null nextKey when response is empty`() = runBlocking {
        val result = pagingSource.load(refreshParams(key = 1)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `load returns incremented nextKey when response has exactly 20 items`() = runBlocking {
        coEvery { api.getCommunity(any()) } returns fakeResponse(List(20) { fakeUserDto(it.toLong()) })

        val result = pagingSource.load(refreshParams(key = 2)) as PagingSource.LoadResult.Page

        assertEquals(3, result.nextKey)
    }

    @Test
    fun `load returns NoConnectivity error when api throws IOException`() = runBlocking {
        coEvery { api.getCommunity(any()) } throws IOException("network unavailable")

        val result = pagingSource.load(refreshParams(key = 1))

        assertEquals(
            CommunityError.NoConnectivity,
            (result as PagingSource.LoadResult.Error).throwable
        )
    }

    @Test
    fun `load returns Timeout error when api throws SocketTimeoutException`() = runBlocking {
        coEvery { api.getCommunity(any()) } throws SocketTimeoutException()

        val result = pagingSource.load(refreshParams(key = 1))

        assertEquals(
            CommunityError.Timeout,
            (result as PagingSource.LoadResult.Error).throwable
        )
    }

    @Test
    fun `load returns HttpError with correct code when api throws HttpException`() = runBlocking {
        val httpException = mockk<HttpException> { every { code() } returns 404 }
        coEvery { api.getCommunity(any()) } throws httpException

        val result = pagingSource.load(refreshParams(key = 1))

        assertEquals(
            CommunityError.HttpError(404),
            (result as PagingSource.LoadResult.Error).throwable
        )
    }

    @Test
    fun `load returns Unknown error when api throws generic Exception`() = runBlocking {
        val cause = RuntimeException("unexpected")
        coEvery { api.getCommunity(any()) } throws cause

        val result = pagingSource.load(refreshParams(key = 1))

        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is CommunityError.Unknown)
        assertEquals(cause, (error as CommunityError.Unknown).throwable)
    }

    @Test
    fun `load rethrows CancellationException instead of wrapping it`() = runBlocking {
        coEvery { api.getCommunity(any()) } throws CancellationException("cancelled")

        var caughtCancellation = false
        try {
            pagingSource.load(refreshParams(key = 1))
        } catch (e: CancellationException) {
            caughtCancellation = true
        }

        assertTrue(caughtCancellation)
    }

    // endregion

    // region getRefreshKey()

    @Test
    fun `getRefreshKey returns null when anchorPosition is null`() {
        val state = PagingState<Int, CommunityUser>(
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
            data = listOf(fakeCommunityUser(1)), prevKey = 2, nextKey = 4
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
            data = listOf(fakeCommunityUser(1)), prevKey = null, nextKey = 2
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
