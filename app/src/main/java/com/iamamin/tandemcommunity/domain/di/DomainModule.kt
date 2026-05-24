package com.iamamin.tandemcommunity.domain.di

import com.iamamin.tandemcommunity.domain.usecase.GetCommunityMembersUseCase
import com.iamamin.tandemcommunity.domain.usecase.ToggleLikeUseCase
import org.koin.dsl.module

val domainModule = module {

    factory {
        GetCommunityMembersUseCase(communityRepository = get(), likedRepository = get())
    }

    factory {
        ToggleLikeUseCase(likedRepository = get())
    }
}
