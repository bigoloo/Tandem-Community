package com.iamamin.tandemcommunity.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val repository: CommunityRepository
) : ViewModel() {

    val members: Flow<PagingData<UserDto>> =
        repository.getCommunityMembers()
            .cachedIn(viewModelScope)

    val likedUserIds: StateFlow<Set<Long>> = repository.observeLikedUserIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun onLikeClicked(userId: Long) {
        viewModelScope.launch {
            repository.toggleLike(userId)
        }
    }
}