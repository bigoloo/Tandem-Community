package com.iamamin.tandemcommunity.di

import com.iamamin.tandemcommunity.data.di.dataModule
import com.iamamin.tandemcommunity.domain.di.domainModule
import com.iamamin.tandemcommunity.presentation.di.uiModel
import org.koin.dsl.module

val appModule = module {

    includes(domainModule, dataModule, uiModel)
}