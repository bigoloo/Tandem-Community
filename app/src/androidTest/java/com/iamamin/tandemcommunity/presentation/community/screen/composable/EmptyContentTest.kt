package com.iamamin.tandemcommunity.presentation.community.screen.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmptyContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    // region content

    @Test
    fun showsNoMembersTitle() {
        composeRule.setContent {
            TandemCommunityTheme { EmptyContent() }
        }

        composeRule.onNodeWithText("No members found").assertIsDisplayed()
    }

    @Test
    fun showsCheckBackSubtitle() {
        composeRule.setContent {
            TandemCommunityTheme { EmptyContent() }
        }

        composeRule.onNodeWithText("Check back later").assertIsDisplayed()
    }

    // endregion
}
