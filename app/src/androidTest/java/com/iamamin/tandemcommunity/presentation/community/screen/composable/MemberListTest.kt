package com.iamamin.tandemcommunity.presentation.community.screen.composable

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.presentation.theme.TandemCommunityTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemberListTest {

    @get:Rule
    val composeRule = createComposeRule()

    // region content

    @Test
    fun showsMemberFirstNames() {
        composeRule.setContent {
            TandemCommunityTheme {
                MemberList(
                    members = flowOf(PagingData.from(fakeMembers())).collectAsLazyPagingItems(),
                    listState = rememberLazyListState(),
                    onLikeClicked = {}
                )
            }
        }

        composeRule.onNodeWithText("Amin").assertIsDisplayed()
        composeRule.onNodeWithText("Sara").assertIsDisplayed()
    }

    @Test
    fun showsUnlikedMemberWithLikeContentDescription() {
        composeRule.setContent {
            TandemCommunityTheme {
                MemberList(
                    members = flowOf(PagingData.from(listOf(fakeMember(isLiked = false)))).collectAsLazyPagingItems(),
                    listState = rememberLazyListState(),
                    onLikeClicked = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Like").assertIsDisplayed()
    }

    @Test
    fun showsLikedMemberWithUnlikeContentDescription() {
        composeRule.setContent {
            TandemCommunityTheme {
                MemberList(
                    members = flowOf(PagingData.from(listOf(fakeMember(isLiked = true)))).collectAsLazyPagingItems(),
                    listState = rememberLazyListState(),
                    onLikeClicked = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Unlike").assertIsDisplayed()
    }

    // endregion

    // region interaction

    @Test
    fun onLikeClickedIsCalledWithCorrectMemberId() {
        var clickedId: Long? = null
        composeRule.setContent {
            TandemCommunityTheme {
                MemberList(
                    members = flowOf(PagingData.from(listOf(fakeMember(id = 42L)))).collectAsLazyPagingItems(),
                    listState = rememberLazyListState(),
                    onLikeClicked = { clickedId = it }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Like").performClick()

        assert(clickedId == 42L)
    }

    // endregion

    // region helpers

    private fun fakeMembers() = listOf(
        fakeMember(id = 1, firstname = "Amin", isLiked = false),
        fakeMember(id = 2, firstname = "Sara", isLiked = true),
    )

    private fun fakeMember(
        id: Long = 1,
        firstname: String = "Name",
        isLiked: Boolean = false,
    ) = CommunityMember(
        id = id,
        firstname = firstname,
        topic = "Topic",
        pictureUrl = "",
        native = "en",
        learn = "de",
        isNew = false,
        isLiked = isLiked,
    )

    // endregion
}
