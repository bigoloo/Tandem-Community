package com.iamamin.tandemcommunity.di

import com.iamamin.tandemcommunity.data.local.di.localModule
import com.iamamin.tandemcommunity.data.remote.di.remoteModule
import org.koin.dsl.module

val appModule = module {

    includes(remoteModule, localModule)
}