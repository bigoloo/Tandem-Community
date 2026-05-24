package com.iamamin.tandemcommunity.presentation.community.screen.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    // region message

    @Test
    fun showsProvidedErrorMessage() {
        composeRule.setContent {
            TandemCommunityTheme {
                ErrorScreen(message = "Something went wrong", onRetry = {})
            }
        }

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    // endregion

    // region retry button

    @Test
    fun retryButtonIsAlwaysDisplayed() {
        composeRule.setContent {
            TandemCommunityTheme {
                ErrorScreen(message = "Error", onRetry = {})
            }
        }

        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun retryButtonInvokesOnRetryCallback() {
        var retryClicked = false
        composeRule.setContent {
            TandemCommunityTheme {
                ErrorScreen(message = "Error", onRetry = { retryClicked = true })
            }
        }

        composeRule.onNodeWithText("Retry").performClick()

        assert(retryClicked)
    }

    // endregion

    // region settings button

    @Test
    fun settingsButtonIsHiddenByDefault() {
        composeRule.setContent {
            TandemCommunityTheme {
                ErrorScreen(message = "Error", onRetry = {})
            }
        }

        composeRule.onNodeWithText("Open Settings").assertIsNotDisplayed()
    }

    @Test
    fun settingsButtonIsShownWhenEnabled() {
        composeRule.setContent {
            TandemCommunityTheme {
                ErrorScreen(message = "Error", showSettingsButton = true, onRetry = {})
            }
        }

        composeRule.onNodeWithText("Open Settings").assertIsDisplayed()
    }

    // endregion
}
