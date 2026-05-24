package com.iamamin.tandemcommunity.presentation.community.screen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.presentation.theme.Spacing
import com.iamamin.tandemcommunity.presentation.utils.ThemedPreviewWrapper
import kotlinx.coroutines.flow.flowOf

@Composable
fun MemberList(
    members: LazyPagingItems<CommunityMember>,
    listState: LazyListState,
    onLikeClicked: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        items(
            count = members.itemCount,
            key = members.itemKey { it.id }
        ) { index ->
            members[index]?.let { member ->
                CommunityMemberCard(
                    member = member,
                    onLikeClicked = { onLikeClicked(member.id) }
                )
            }
        }

        if (members.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(Spacing.lg)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@PreviewWrapper(ThemedPreviewWrapper::class)
@Composable
private fun MemberListPreview() {
    MemberList(
        members = flowOf(PagingData.from(fakeMemberListPreviewData())).collectAsLazyPagingItems(),
        listState = rememberLazyListState(),
        onLikeClicked = {}
    )
}

private fun fakeMemberListPreviewData() = listOf(
    CommunityMember(
        id = 1,
        firstname = "Amin",
        topic = "I want to learn Android development and share my knowledge about Kotlin.",
        pictureUrl = "https://tandem2019.web.app/img/pic1.png",
        native = "Persian",
        learn = "German",
        isNew = true,
        isLiked = false,
    ),
    CommunityMember(
        id = 2,
        firstname = "Sara",
        topic = "Looking for a language partner to practice Spanish.",
        pictureUrl = "https://tandem2019.web.app/img/pic2.png",
        native = "English",
        learn = "Spanish",
        isNew = false,
        isLiked = true,
    ),
    CommunityMember(
        id = 3,
        firstname = "John",
        topic = "Passionate about learning Mandarin and teaching French.",
        pictureUrl = "https://tandem2019.web.app/img/pic2.png",
        native = "French",
        learn = "Mandarin",
        isNew = false,
        isLiked = false,
    ),
)
