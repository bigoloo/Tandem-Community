package com.iamamin.tandemcommunity.domain.model.error

sealed class CommunityError : Exception() {
    data object NoConnectivity : CommunityError()
    data object Timeout : CommunityError()
    data class HttpError(val code: Int) : CommunityError()
    data class Unknown(val throwable: Throwable) : CommunityError()
}
