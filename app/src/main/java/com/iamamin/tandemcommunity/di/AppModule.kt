package com.iamamin.tandemcommunity.di

import com.iamamin.tandemcommunity.data.di.dataModule
import com.iamamin.tandemcommunity.presentation.di.uiModel
import org.koin.dsl.module

val appModule = module {

    includes(dataModule, uiModel)
}