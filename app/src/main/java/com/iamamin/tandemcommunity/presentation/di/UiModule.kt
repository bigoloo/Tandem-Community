package com.iamamin.tandemcommunity.presentation.di

import com.iamamin.tandemcommunity.presentation.community.CommunityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModel = module {
    viewModel {
        CommunityViewModel(
            getCommunityMembersUseCase = get(),
            toggleLikeUseCase = get(),
            connectivityObserver = get(),
            eventLogger = get(),
        )
    }
}