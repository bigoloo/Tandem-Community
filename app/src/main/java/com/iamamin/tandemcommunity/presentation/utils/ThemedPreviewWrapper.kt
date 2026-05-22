package com.iamamin.tandemcommunity.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme


class ThemedPreviewWrapper : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        TandemCommunityTheme {
             content()
        }
    }
}
