package com.iamamin.tandemcommunity.data.di

import com.iamamin.tandemcommunity.data.connectivity.NetworkConnectivityObserver
import com.iamamin.tandemcommunity.data.local.di.localModule
import com.iamamin.tandemcommunity.data.remote.di.remoteModule
import com.iamamin.tandemcommunity.data.repository.CommunityRepositoryImpl
import com.iamamin.tandemcommunity.data.repository.LikedRepositoryImpl
import com.iamamin.tandemcommunity.domain.connectivity.ConnectivityObserver
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import com.iamamin.tandemcommunity.domain.repository.LikedRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    includes(remoteModule, localModule)

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(context = androidContext())
    }

    single<CommunityRepository> {
        CommunityRepositoryImpl(api = get())
    }

    single<LikedRepository> {
        LikedRepositoryImpl(likedUserDao = get())
    }
}