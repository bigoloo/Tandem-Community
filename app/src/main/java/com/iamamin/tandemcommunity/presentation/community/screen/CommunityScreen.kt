package com.iamamin.tandemcommunity.presentation.community.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.iamamin.tandemcommunity.R
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.iamamin.tandemcommunity.presentation.community.CommunityViewModel
import com.iamamin.tandemcommunity.presentation.community.isNoConnectivity
import com.iamamin.tandemcommunity.presentation.community.screen.composable.CommunityMemberCard
import com.iamamin.tandemcommunity.presentation.community.screen.composable.EmptyContent
import com.iamamin.tandemcommunity.presentation.community.screen.composable.ErrorScreen
import com.iamamin.tandemcommunity.presentation.community.toCommunityMessage
import com.iamamin.tandemcommunity.presentation.utils.SnackbarEvent.Dismiss
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = koinViewModel()
) {
    val members = viewModel.members.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch {
            snapshotFlow { members.loadState.append }
                .collectLatest { appendState ->
                    if (appendState is LoadState.Error) {
                        val result = snackbarHostState.showSnackbar(
                            message = appendState.error.toCommunityMessage(context),
                            actionLabel = context.getString(R.string.action_retry),
                            duration = SnackbarDuration.Indefinite
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            members.retry()
                        }
                    }
                }
        }
        viewModel.snackbarEvent.collect { event ->
            when (event) {
                is Dismiss -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    when {
                        members.loadState.refresh is LoadState.Error -> members.refresh()
                        members.loadState.append is LoadState.Error -> members.retry()
                    }
                }
            }
        }
    }

    val isAtEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible != null && lastVisible >= total - 2
        }
    }
    LaunchedEffect(isAtEnd) {
        if (isAtEnd && members.loadState.append is LoadState.Error) {
            members.retry()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val refreshState = members.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is LoadState.Error -> {
                    ErrorScreen(
                        message = refreshState.error.toCommunityMessage(context),
                        showSettingsButton = refreshState.error.isNoConnectivity(),
                        onRetry = { members.refresh() },
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                    )
                }

                is LoadState.NotLoading -> {
                    if (members.itemCount == 0) {
                        EmptyContent(modifier = Modifier.fillMaxSize())
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}