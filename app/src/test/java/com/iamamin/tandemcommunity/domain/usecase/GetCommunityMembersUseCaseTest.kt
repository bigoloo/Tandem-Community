package com.iamamin.tandemcommunity.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.domain.model.CommunityUser
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import com.iamamin.tandemcommunity.domain.repository.LikedRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCommunityMembersUseCaseTest {

    private val communityRepository = mockk<CommunityRepository>()
    private val likedRepository = mockk<LikedRepository>()

    private val useCase = GetCommunityMembersUseCase(communityRepository, likedRepository)

    @Test
    fun `maps user fields and picks first native and learn`() = runTest(UnconfinedTestDispatcher()) {
        val user = userFixture(
            id = 1,
            firstname = "Amin",
            topic = "Android",
            pictureUrl = "https://x/1.png",
            natives = listOf("en", "fa"),
            learns = listOf("de", "fr"),
            referenceCnt = 3,
        )
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf(user)
        every { likedRepository.observeLikedUserIds() } returns flowOf(emptySet())

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(
            listOf(
                CommunityMember(
                    id = 1,
                    firstname = "Amin",
                    topic = "Android",
                    pictureUrl = "https://x/1.png",
                    native = "en",
                    learn = "de",
                    isNew = false,
                    isLiked = false,
                )
            ),
            result,
        )
    }

    @Test
    fun `isLiked is true when user id is in liked ids`() = runTest(UnconfinedTestDispatcher()) {
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf(userFixture(id = 1))
        every { likedRepository.observeLikedUserIds() } returns flowOf(setOf(1))

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(true, result.single().isLiked)
    }

    @Test
    fun `isLiked is false when user id is not in liked ids`() = runTest(UnconfinedTestDispatcher()) {
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf(userFixture(id = 1))
        every { likedRepository.observeLikedUserIds() } returns flowOf(setOf(2, 3))

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(false, result.single().isLiked)
    }

    @Test
    fun `isNew is true when referenceCnt is zero`() = runTest(UnconfinedTestDispatcher()) {
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf(userFixture(referenceCnt = 0))
        every { likedRepository.observeLikedUserIds() } returns flowOf(emptySet())

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(true, result.single().isNew)
    }

    @Test
    fun `isNew is false when referenceCnt is greater than zero`() = runTest(UnconfinedTestDispatcher()) {
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf(userFixture(referenceCnt = 1))
        every { likedRepository.observeLikedUserIds() } returns flowOf(emptySet())

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(false, result.single().isNew)
    }

    @Test
    fun `maps multiple users preserving order and per-item liked state`() = runTest(UnconfinedTestDispatcher()) {
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf(
            userFixture(id = 1), userFixture(id = 2), userFixture(id = 3)
        )
        every { likedRepository.observeLikedUserIds() } returns flowOf(setOf(2))

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(listOf<Long>(1, 2, 3), result.map { it.id })
        assertEquals(listOf(false, true, false), result.map { it.isLiked })
    }

    @Test
    fun `emits empty list when no users`() = runTest(UnconfinedTestDispatcher()) {
        every { communityRepository.getCommunityUsers() } returns pagingFlowOf()
        every { likedRepository.observeLikedUserIds() } returns flowOf(emptySet())

        val result = useCase(backgroundScope).asSnapshot()

        assertEquals(emptyList<CommunityMember>(), result)
    }

    private fun pagingFlowOf(vararg users: CommunityUser): Flow<PagingData<CommunityUser>> =
        Pager(PagingConfig(pageSize = 20)) {
            object : PagingSource<Int, CommunityUser>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommunityUser> =
                    LoadResult.Page(data = users.toList(), prevKey = null, nextKey = null)
                override fun getRefreshKey(state: PagingState<Int, CommunityUser>): Int? = null
            }
        }.flow

    private fun userFixture(
        id: Long = 1,
        firstname: String = "Name",
        topic: String = "Topic",
        pictureUrl: String = "url",
        natives: List<String> = listOf("en"),
        learns: List<String> = listOf("de"),
        referenceCnt: Int = 5,
    ) = CommunityUser(
        id = id,
        firstname = firstname,
        topic = topic,
        pictureUrl = pictureUrl,
        natives = natives,
        learns = learns,
        referenceCnt = referenceCnt,
    )
}
