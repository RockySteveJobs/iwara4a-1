package com.rerere.iwara4a.ui.screen.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.friends.Friend
import com.rerere.iwara4a.model.friends.FriendStatus
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun FriendsScreen(friendsViewModel: FriendsViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(text = stringResource(id = R.string.screen_friends_topbar_title))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            FriendsList(friendsViewModel)
        }
    }
}

@Composable
private fun FriendsList(
    friendsViewModel: FriendsViewModel
) {
    val friendList by friendsViewModel.friendList.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = friendList is DataState.Loading
    )
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            friendsViewModel.loadFriendList()
        }
    ) {
        when (friendList) {
            is DataState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {
                            friendsViewModel.loadFriendList()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.load_error), fontSize = 20.sp)
                }
            }
            else -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    friendList.readSafely()?.takeIf { it.isEmpty() }?.let {
                        item {
                            Text(
                                text = stringResource(id = R.string.screen_friends_list_empty), modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), textAlign = TextAlign.Center
                            )
                        }
                    }
                    friendList.readSafely()?.groupBy { it.friendStatus }
                        ?.forEach { (status, list) ->
                            stickyHeader {
                                Surface(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val header = when (status) {
                                        FriendStatus.PENDING -> stringResource(R.string.screen_friends_list_status_pending)
                                        FriendStatus.PENDING_REQUEST -> stringResource(R.string.screen_friends_list_status_pending_request)
                                        FriendStatus.ACCEPTED -> stringResource(R.string.screen_friends_list_status_accepted)
                                        else -> stringResource(R.string.screen_friends_list_status_else)
                                    }
                                    Text(
                                        text = header,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            items(list) {
                                FriendItem(friendsViewModel, it)
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun FriendItem(
    friendsViewModel: FriendsViewModel,
    friend: Friend
) {
    val navController = LocalNavController.current
    val deleteDialog = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = deleteDialog,
        buttons = {
            positiveButton(stringResource(id = R.string.sure_button)) {
                deleteDialog.hide()
                friendsViewModel.handleFriendRequest(friend.frId, false) {
                    friendsViewModel.loadFriendList()
                }
            }
            negativeButton(stringResource(id = R.string.cancel_button)){
                deleteDialog.hide()
            }
        }
    ) {
        title(stringResource(id = R.string.screen_friends_item_title))
        message("${stringResource(id = R.string.screen_friends_item_message)} ${friend.username}")
    }
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("user/${friend.userId}")
            }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = friend.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = friend.date)
            }

            when (friend.friendStatus) {
                FriendStatus.PENDING -> {
                    IconButton(onClick = {
                        friendsViewModel.handleFriendRequest(friend.frId, true) {
                            friendsViewModel.loadFriendList()
                        }
                    }) {
                        Icon(Icons.Default.Check, null)
                    }
                    IconButton(onClick = {
                        friendsViewModel.handleFriendRequest(friend.frId, false) {
                            friendsViewModel.loadFriendList()
                        }
                    }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
                FriendStatus.PENDING_REQUEST -> {
                    IconButton(onClick = {
                        friendsViewModel.handleFriendRequest(friend.frId, false) {
                            friendsViewModel.loadFriendList()
                        }
                    }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
                FriendStatus.ACCEPTED -> {
                    IconButton(onClick = {
                        deleteDialog.show()
                    }) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
                else -> {
                    Text(text = stringResource(id = R.string.screen_friends_item_unknown_mistake))
                }
            }
        }
    }
}