package com.iamamin.tandemcommunity.presentation.community

import android.content.Context
import com.iamamin.tandemcommunity.R
import com.iamamin.tandemcommunity.domain.model.error.CommunityError

fun Throwable.toCommunityMessage(context: Context): String = when (this) {
    is CommunityError.NoConnectivity -> context.getString(R.string.error_no_connectivity)
    is CommunityError.Timeout -> context.getString(R.string.error_timeout)
    is CommunityError.HttpError -> context.getString(R.string.error_http, code)
    else -> context.getString(R.string.error_unknown)
}

fun Throwable.isNoConnectivity(): Boolean = this is CommunityError.NoConnectivity
