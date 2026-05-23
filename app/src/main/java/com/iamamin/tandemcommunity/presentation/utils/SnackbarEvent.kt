package com.iamamin.tandemcommunity.presentation.utils

sealed class SnackbarEvent {
    data object Dismiss : SnackbarEvent()
}
