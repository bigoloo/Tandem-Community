package com.iamamin.tandemcommunity.presentation.community.screen.composable

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.iamamin.tandemcommunity.R
import com.iamamin.tandemcommunity.presentation.utils.ThemedPreviewWrapper

@Composable
fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier,
    showSettingsButton: Boolean = false,
    onRetry: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
            if (showSettingsButton) {
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                    }
                ) {
                    Text(stringResource(R.string.action_open_settings))
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@PreviewWrapper(ThemedPreviewWrapper::class)
@Composable
private fun ErrorScreenConnectivityPreview() {
    ErrorScreen(
        message = "No internet connection",
        showSettingsButton = true,
        onRetry = {}
    )
}

@Preview(showBackground = true, apiLevel = 34)
@PreviewWrapper(ThemedPreviewWrapper::class)
@Composable
private fun ErrorScreenHttpPreview() {
    ErrorScreen(
        message = "Server error (500)",
        onRetry = {}
    )
}
