package com.rerere.iwara4a.ui.screen.chat

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.EmojiSelector
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.public.SmartLinkText
import com.rerere.iwara4a.ui.public.parseUrls
import com.rerere.iwara4a.util.DataState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val userData by chatViewModel.userData.collectAsState()
    Scaffold(
        topBar = {
            FullScreenTopBar(
                title = {
                    Text(text = "聊天室")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    if (userData is DataState.Loading) {
                        Text(text = "加载中")
                    } else if (userData is DataState.Error) {
                        TextButton(onClick = {
                            chatViewModel.fetchUserData()
                        }) {
                            Text(text = "重连")
                        }
                    } else if (!chatViewModel.connectionOpened) {
                        TextButton(onClick = {
                            chatViewModel.fetchUserData()
                        }) {
                            Text(text = "重连")
                        }
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.navigationBarsWithImePadding()) {
            ChatBody(
                navController = navController,
                chatViewModel = chatViewModel
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ChatBody(
    navController: NavController,
    chatViewModel: ChatViewModel
) {
    var content by remember {
        mutableStateOf("")
    }
    val conetxt = LocalContext.current
    val user by chatViewModel.userData.collectAsState()
    val focusManager = LocalFocusManager.current
    Column {
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

        Surface(modifier = Modifier.fillMaxWidth(), elevation = 16.dp) {
            var showEmojiSelector by remember {
                mutableStateOf(false)
            }
            BackHandler(showEmojiSelector) {
                showEmojiSelector = false
            }
            Column {
                // 输入栏
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    // 输入框
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                        },
                        shape = RoundedCornerShape(25),
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            IconButton(onClick = { showEmojiSelector = !showEmojiSelector }) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEmotions,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                if (content.isBlank()) {
                                    Toast.makeText(conetxt, "内容不能为空！", Toast.LENGTH_SHORT).show()
                                    return@IconButton
                                }
                                chatViewModel.send(content) {
                                    if (!it) {
                                        Toast.makeText(conetxt, "发送失败！", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                content = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 2,
                        placeholder = {
                            Text(text = "请文明用语哦")
                        }
                    )
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
                            text = "开发者",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(1.dp)
                                .background(
                                    color = MaterialTheme.colors.primary,
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
                                        color = MaterialTheme.colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    it
                                }
                            })
                    }
                }

                // Message
                SelectionContainer(
                    Modifier
                        .drawBehind {
                            val bubble = Path().apply {
                                val rect = RoundRect(
                                    10.dp.toPx(),
                                    0f,
                                    size.width - 10.dp.toPx(),
                                    size.height,
                                    4.dp.toPx(),
                                    4.dp.toPx()
                                )
                                addRoundRect(rect)
                                moveTo(size.width - 10.dp.toPx(), 15.dp.toPx())
                                lineTo(size.width - 5.dp.toPx(), 20.dp.toPx())
                                lineTo(size.width - 10.dp.toPx(), 25.dp.toPx())
                                close()
                            }
                            drawPath(bubble, Color.Blue.copy(alpha = 0.3f))
                        }
                        .padding(20.dp, 10.dp)) {
                    CompositionLocalProvider(
                        LocalTextStyle provides LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.onSurface
                        )
                    ) {
                        SmartLinkText(text = chatMessage.message, maxLines = 10)
                    }
                }

                // Preview
                chatMessage.message.parseUrls().find { it.isImage() }?.let {
                    val painter = rememberImagePainter(it.text)
                    if (painter.state !is ImagePainter.State.Error) {
                        Image(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .fillMaxWidth(0.7f)
                                .heightIn(max = 130.dp)
                                .placeholder(
                                    visible = painter.state is ImagePainter.State.Loading,
                                    highlight = PlaceholderHighlight.shimmer()
                                ),
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth
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
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberImagePainter(chatMessage.avatar),
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
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberImagePainter(chatMessage.avatar),
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
                            text = "开发者",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(1.dp)
                                .background(
                                    color = MaterialTheme.colors.primary,
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
                                        color = MaterialTheme.colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    it
                                }
                            })
                    }
                }
                SelectionContainer(
                    Modifier
                        .drawBehind {
                            val bubble = Path().apply {
                                val rect = RoundRect(
                                    10.dp.toPx(),
                                    0f,
                                    size.width - 10.dp.toPx(),
                                    size.height,
                                    4.dp.toPx(),
                                    4.dp.toPx()
                                )
                                addRoundRect(rect)
                                moveTo(10.dp.toPx(), 15.dp.toPx())
                                lineTo(5.dp.toPx(), 20.dp.toPx())
                                lineTo(10.dp.toPx(), 25.dp.toPx())
                                close()
                            }
                            drawPath(bubble, Color.LightGray.copy(alpha = 0.3f))
                        }
                        .padding(20.dp, 10.dp)
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.onSurface
                        )
                    ) {
                        SmartLinkText(text = chatMessage.message, maxLines = 10)
                    }
                }
                // Preview
                chatMessage.message.parseUrls().find { it.isImage() }?.let {
                    val painter = rememberImagePainter(it.text)
                    if (painter.state !is ImagePainter.State.Error) {
                        Image(
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .fillMaxWidth(0.7f)
                                .heightIn(max = 130.dp)
                                .placeholder(
                                    visible = painter.state is ImagePainter.State.Loading,
                                    highlight = PlaceholderHighlight.shimmer()
                                ),
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
}