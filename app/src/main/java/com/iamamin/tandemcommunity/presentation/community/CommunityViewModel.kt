package com.iamamin.tandemcommunity.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.iamamin.tandemcommunity.domain.analytics.AppEvent
import com.iamamin.tandemcommunity.domain.analytics.EventLogger
import com.iamamin.tandemcommunity.domain.connectivity.ConnectivityObserver
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.domain.usecase.GetCommunityMembersUseCase
import com.iamamin.tandemcommunity.domain.usecase.ToggleLikeUseCase
import com.iamamin.tandemcommunity.presentation.utils.SnackbarEvent
import com.iamamin.tandemcommunity.presentation.utils.SnackbarEvent.Dismiss
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class CommunityViewModel(
    getCommunityMembersUseCase: GetCommunityMembersUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    connectivityObserver: ConnectivityObserver,
    private val eventLogger: EventLogger,
) : ViewModel() {

    val members: Flow<PagingData<CommunityMember>> =
        getCommunityMembersUseCase(viewModelScope)

    private val _snackbarEvent = MutableSharedFlow<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            connectivityObserver.isConnected
                .drop(1)
                .collect { isConnected ->
                    if (isConnected) {
                        eventLogger.log(AppEvent.ConnectivityRestored)
                        _snackbarEvent.emit(Dismiss)
                    }
                }
        }
    }

    fun onLikeClicked(userId: Long) {
        eventLogger.log(AppEvent.MemberLikeToggled(userId))
        viewModelScope.launch {
            toggleLikeUseCase(userId)
        }
    }
}