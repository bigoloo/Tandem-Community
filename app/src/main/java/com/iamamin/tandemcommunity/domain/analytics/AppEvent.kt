package com.iamamin.tandemcommunity.domain.analytics

sealed class AppEvent {
    data class MemberLikeToggled(val userId: Long) : AppEvent()
    data object ConnectivityRestored : AppEvent()
}
