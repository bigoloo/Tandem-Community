package com.iamamin.tandemcommunity.presentation.community.screen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.iamamin.tandemcommunity.presentation.theme.AppTypography
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme
import com.iamamin.tandemcommunity.presentation.utils.ThemedPreviewWrapper

@Composable
fun LanguageLabel(label: String, language: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label, style = AppTypography.labelSmall, color = Color(0xFF7BC8A4)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = language,
            style = AppTypography.labelSmall,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
@PreviewWrapper(ThemedPreviewWrapper::class)
private fun LanguageLabelPreview() {
        LanguageLabel(
            label = "NATIVE",
            language = "English"
        )
}