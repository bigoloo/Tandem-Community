package com.iamamin.tandemcommunity.presentation.community

import com.iamamin.tandemcommunity.domain.model.error.CommunityError

fun Throwable.toCommunityMessage(): String = when (this) {
    is CommunityError.NoConnectivity -> "No internet connection"
    is CommunityError.Timeout -> "Request timed out"
    is CommunityError.HttpError -> "Server error ($code)"
    else -> "Something went wrong"
}

fun Throwable.isNoConnectivity(): Boolean = this is CommunityError.NoConnectivity
