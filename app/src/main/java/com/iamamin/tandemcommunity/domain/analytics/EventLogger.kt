package com.iamamin.tandemcommunity.domain.analytics

interface EventLogger {
    fun log(event: AppEvent)
}
