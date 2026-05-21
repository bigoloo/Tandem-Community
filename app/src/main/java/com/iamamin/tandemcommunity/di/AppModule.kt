package com.iamamin.tandemcommunity.di

import com.iamamin.tandemcommunity.data.di.dataModule
import org.koin.dsl.module

val appModule = module {

    includes(dataModule)
}