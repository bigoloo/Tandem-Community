package com.iamamin.tandemcommunity.presentation.community

import androidx.paging.PagingData
import com.iamamin.tandemcommunity.domain.analytics.AppEvent
import com.iamamin.tandemcommunity.domain.analytics.EventLogger
import com.iamamin.tandemcommunity.domain.connectivity.ConnectivityObserver
import com.iamamin.tandemcommunity.domain.usecase.GetCommunityMembersUseCase
import com.iamamin.tandemcommunity.domain.usecase.ToggleLikeUseCase
import com.iamamin.tandemcommunity.presentation.utils.SnackbarEvent
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val connectivityFlow = MutableStateFlow(false)

    private val getCommunityMembersUseCase = mockk<GetCommunityMembersUseCase>()
    private val toggleLikeUseCase = mockk<ToggleLikeUseCase>(relaxed = true)
    private val connectivityObserver = mockk<ConnectivityObserver> {
        every { isConnected } returns connectivityFlow
    }
    private val eventLogger = mockk<EventLogger>(relaxed = true)

    private lateinit var viewModel: CommunityViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getCommunityMembersUseCase(any()) } returns flowOf(PagingData.empty())
        viewModel = CommunityViewModel(
            getCommunityMembersUseCase,
            toggleLikeUseCase,
            connectivityObserver,
            eventLogger,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // region onLikeClicked()

    @Test
    fun `onLikeClicked logs MemberLikeToggled event with the given userId`() = runTest(testDispatcher) {
        viewModel.onLikeClicked(42L)

        verify { eventLogger.log(AppEvent.MemberLikeToggled(42L)) }
    }

    @Test
    fun `onLikeClicked calls toggleLikeUseCase with the given userId`() = runTest(testDispatcher) {
        viewModel.onLikeClicked(42L)
        advanceUntilIdle()

        coVerify { toggleLikeUseCase(42L) }
    }

    // endregion

    // region connectivity

    @Test
    fun `initial connectivity emission is dropped and emits no snackbar`() = runTest(testDispatcher) {
        val events = mutableListOf<SnackbarEvent>()
        val job = launch { viewModel.snackbarEvent.collect { events.add(it) } }
        advanceUntilIdle()
        job.cancel()

        assertTrue(events.isEmpty())
    }

    @Test
    fun `reconnection emits Dismiss snackbar`() = runTest(testDispatcher) {
        val events = mutableListOf<SnackbarEvent>()
        val job = launch { viewModel.snackbarEvent.collect { events.add(it) } }

        connectivityFlow.value = true
        advanceUntilIdle()
        job.cancel()

        assertEquals(listOf(SnackbarEvent.Dismiss), events)
    }

    @Test
    fun `reconnection logs ConnectivityRestored event`() = runTest(testDispatcher) {
        connectivityFlow.value = true
        advanceUntilIdle()

        verify { eventLogger.log(AppEvent.ConnectivityRestored) }
    }

    @Test
    fun `multiple reconnections each emit Dismiss and false emissions in between are ignored`() = runTest(testDispatcher) {
        val events = mutableListOf<SnackbarEvent>()
        val job = launch { viewModel.snackbarEvent.collect { events.add(it) } }

        connectivityFlow.value = true
        connectivityFlow.value = false
        connectivityFlow.value = true
        advanceUntilIdle()
        job.cancel()

        assertEquals(2, events.size)
        assertTrue(events.all { it == SnackbarEvent.Dismiss })
    }

    // endregion
}
