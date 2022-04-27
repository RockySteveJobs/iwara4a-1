package com.rerere.iwara4a.ui.screen.chat

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.EmojiSelector
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.SmartLinkText
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val userData by chatViewModel.userData.collectAsState()
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(text = stringResource(id = R.string.screen_chat_topbar_title))
                },
                navigationIcon = {
                   BackIcon()
                },
                actions = {
                    if (userData is DataState.Loading) {
                        Text(text = stringResource(id = R.string.loading))
                    } else if (userData is DataState.Error) {
                        TextButton(onClick = {
                            chatViewModel.fetchUserData()
                        }) {
                            Text(text = stringResource(id = R.string.screen_chat_reconnection))
                        }
                    } else if (!chatViewModel.connectionOpened) {
                        TextButton(onClick = {
                            chatViewModel.fetchUserData()
                        }) {
                            Text(text = stringResource(id = R.string.screen_chat_reconnection))
                        }
                    }
                }
            )
        }
    ) {
        ChatBody(
            chatViewModel = chatViewModel,
            padding = it
        )
    }
}

@Composable
private fun ChatBody(
    chatViewModel: ChatViewModel,
    padding: PaddingValues
) {
    var content by remember { mutableStateOf("") }
    val context = LocalContext.current
    val user by chatViewModel.userData.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize().padding(padding)
    ) {
        // 聊天内容
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true
        ) {
            if (user is DataState.Success) {
                items(chatViewModel.chats.reversed()) {
                    ChatItem(it, it.userId == user.read().id)
                }
            }
        }

        Surface(tonalElevation = 2.dp) {
            Column(modifier = Modifier
                .navigationBarsPadding()
                .imePadding()) {
                // 输入框
                var showEmojiSelector by remember {
                    mutableStateOf(false)
                }
                val focusRequest = FocusRequester()
                BackHandler(showEmojiSelector) {
                    showEmojiSelector = false
                }

                // 输入栏
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .onFocusChanged {
                                showEmojiSelector = it.isFocused
                            }
                            .focusOrder(focusRequest)
                            .focusable(),
                        onClick = {
                            focusRequest.requestFocus()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EmojiEmotions,
                            contentDescription = null
                        )
                    }

                    // 输入框
                    TextField(
                        value = content,
                        onValueChange = {
                            content = it
                        },
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showEmojiSelector = false
                            },
                        maxLines = 2,
                        colors = TextFieldDefaults.textFieldColors(
                            unfocusedIndicatorColor = Color.Transparent,
                            // focusedIndicatorColor = Color.Transparent,
                            containerColor = Color.Transparent
                        ),
                        placeholder = {
                            Text("和大家说点什么吧")
                        }
                    )

                    IconButton(onClick = {
                        if (content.isBlank()) {
                            Toast.makeText(
                                context,
                                context.stringResource(id = R.string.screen_chat_body_content_not_blank),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@IconButton
                        }
                        chatViewModel.send(content) {
                            if (!it) {
                                Toast.makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_chat_body_failed_to_send),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        content = ""
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = null
                        )
                    }
                }
                AnimatedVisibility(showEmojiSelector) {
                    EmojiSelector {
                        focusManager.clearFocus()
                        content += it
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatItem(chatMessage: ChatMessage, self: Boolean) {
    val navController = LocalNavController.current
    if (self) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Nickname
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    if (chatMessage.developer) {
                        Text(
                            text = stringResource(id = R.string.screen_chat_item_developer),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(1.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .padding(1.dp),
                        )
                    }
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            chatMessage.username,
                            fontSize = 13.sp,
                            style = LocalTextStyle.current.let {
                                if (chatMessage.developer) {
                                    it.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    it
                                }
                            })
                    }
                }

                // Message
                Surface(
                    shape = RoundedCornerShape(15.dp, 5.dp, 15.dp, 15.dp),
                    tonalElevation = 8.dp
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        SmartLinkText(
                            modifier = Modifier.padding(16.dp),
                            text = chatMessage.message,
                            maxLines = 10
                        )
                    }
                }
            }

            // Profile Pic
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = chatMessage.avatar,
                    contentDescription = null
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("user/${chatMessage.userId}")
                    }
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = chatMessage.avatar,
                    contentDescription = null
                )
            }
            Column(modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.Start) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    if (chatMessage.developer) {
                        Text(
                            text = stringResource(id = R.string.screen_chat_item_developer),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(1.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .padding(1.dp),
                        )
                    }
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            chatMessage.username,
                            fontSize = 13.sp,
                            style = LocalTextStyle.current.let {
                                if (chatMessage.developer) {
                                    it.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    it
                                }
                            })
                    }
                }
                Surface(
                    shape = RoundedCornerShape(5.dp, 15.dp, 15.dp, 15.dp),
                    tonalElevation = 8.dp
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        SmartLinkText(
                            modifier = Modifier.padding(16.dp),
                            text = chatMessage.message,
                            maxLines = 10
                        )
                    }
                }
            }
        }
    }
}