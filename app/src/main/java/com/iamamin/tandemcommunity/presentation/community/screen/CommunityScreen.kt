package com.iamamin.tandemcommunity.presentation.community.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.presentation.community.CommunityViewModel
import com.iamamin.tandemcommunity.presentation.community.screen.composable.CommunityMemberCard
import com.iamamin.tandemcommunity.presentation.community.screen.composable.LanguageLabel
import com.iamamin.tandemcommunity.presentation.theme.AppTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = koinViewModel()
) {
    val members = viewModel.members.collectAsLazyPagingItems()
    val likedIds by viewModel.likedUserIds.collectAsStateWithLifecycle()

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (members.loadState.refresh) {
                is LoadState.Loading -> {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    //item { ErrorItem(onRetry = { members.retry() }) }
                }

                is LoadState.NotLoading -> Unit
            }

            items(
                count = members.itemCount,
                key = members.itemKey { it.id }
            ) { index ->
                members[index]?.let { user ->
                    CommunityMemberCard(
                        user = user,
                        isLiked = user.id in likedIds,
                        onLikeClicked = { viewModel.onLikeClicked(user.id) }
                    )
                }
            }

            if (members.loadState.append is LoadState.Loading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AsyncImage(model: String, contentDescription: String) {
    ///   TODO("Not yet implemented")
}