package com.iamamin.tandemcommunity.presentation.community.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.iamamin.tandemcommunity.presentation.community.CommunityViewModel
import com.iamamin.tandemcommunity.presentation.community.screen.composable.CommunityMemberCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = koinViewModel()
) {
    val members = viewModel.members.collectAsLazyPagingItems()
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
                        Box(Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center) {
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
                members[index]?.let { member ->
                    CommunityMemberCard(
                        member = member,
                        onLikeClicked = { viewModel.onLikeClicked(member.id) }
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