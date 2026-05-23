package com.iamamin.tandemcommunity.presentation.utils

sealed class SnackbarEvent {
    data class Show(val message: String) : SnackbarEvent()
    data object Dismiss : SnackbarEvent()
}
