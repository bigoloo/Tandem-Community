package com.iamamin.tandemcommunity.presentation.community.screen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.iamamin.tandemcommunity.presentation.theme.appTypography
import com.iamamin.tandemcommunity.presentation.theme.Spacing
import com.iamamin.tandemcommunity.presentation.utils.ThemedPreviewWrapper

@Composable
fun LanguageLabel(label: String, language: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = appTypography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.width(Spacing.xs))
        Text(
            text = language.uppercase(),
            style = appTypography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
@PreviewWrapper(ThemedPreviewWrapper::class)
private fun LanguageLabelPreview() {
    LanguageLabel(
        label = "Native",
        language = "en"
    )
}
