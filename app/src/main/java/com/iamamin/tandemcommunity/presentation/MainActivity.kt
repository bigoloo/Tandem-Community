package com.iamamin.tandemcommunity.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.iamamin.tandemcommunity.presentation.community.screen.CommunityScreen
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TandemCommunityTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    CommunityScreen()
                }
            }
        }
    }
}