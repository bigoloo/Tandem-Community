package com.iamamin.tandemcommunity.data.analytics

import android.util.Log
import com.iamamin.tandemcommunity.domain.analytics.AppEvent
import com.iamamin.tandemcommunity.domain.analytics.EventLogger

class LogcatEventLogger : EventLogger {

    override fun log(event: AppEvent) {
        Log.d(TAG, event.toLogMessage())
    }

    private fun AppEvent.toLogMessage(): String = when (this) {
        is AppEvent.MemberLikeToggled -> "MemberLikeToggled(userId=$userId)"
        AppEvent.ConnectivityRestored -> "ConnectivityRestored"
    }

    private companion object {
        const val TAG = "TandemEvent"
    }
}
