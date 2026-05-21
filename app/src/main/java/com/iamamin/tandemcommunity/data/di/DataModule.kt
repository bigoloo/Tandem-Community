package com.iamamin.tandemcommunity.data.di

import com.iamamin.tandemcommunity.data.local.di.localModule
import com.iamamin.tandemcommunity.data.paging.CommunityPagingSource
import com.iamamin.tandemcommunity.data.remote.di.remoteModule
import com.iamamin.tandemcommunity.data.repository.CommunityRepositoryImpl
import com.iamamin.tandemcommunity.data.repository.LikedRepositoryImpl
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import com.iamamin.tandemcommunity.domain.repository.LikedRepository
import org.koin.dsl.module

val dataModule = module {
    includes(remoteModule, localModule)

    single<CommunityRepository> {
        CommunityRepositoryImpl(api = get())
    }

    single<LikedRepository> {
        LikedRepositoryImpl(likedUserDao = get())
    }

    single {
        CommunityPagingSource(api = get())
    }
}