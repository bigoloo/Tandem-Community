package com.iamamin.tandemcommunity.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.domain.usecase.GetCommunityMembersUseCase
import com.iamamin.tandemcommunity.domain.usecase.ToggleLikeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CommunityViewModel(
    getCommunityMembersUseCase: GetCommunityMembersUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase
) : ViewModel() {

    val members: Flow<PagingData<CommunityMember>> =
        getCommunityMembersUseCase(viewModelScope)

    fun onLikeClicked(userId: Long) {
        viewModelScope.launch {
            toggleLikeUseCase(userId)
        }
    }
}