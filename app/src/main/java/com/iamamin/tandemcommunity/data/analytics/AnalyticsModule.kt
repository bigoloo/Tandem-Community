package com.iamamin.tandemcommunity.data.analytics

import com.iamamin.tandemcommunity.domain.analytics.EventLogger
import org.koin.dsl.module

val analyticsModule = module {
    single<EventLogger> { LogcatEventLogger() }
}
