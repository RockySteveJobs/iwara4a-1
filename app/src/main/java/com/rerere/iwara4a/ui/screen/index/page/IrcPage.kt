package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.SmartLinkText
import com.rerere.iwara4a.ui.public.parseUrls
import com.rerere.iwara4a.ui.screen.index.IndexViewModel

val EMOJI_LIST = "".toCharArray().toList()

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun IRCPage(navController: NavController, indexViewModel: IndexViewModel) {
    var text by rememberSaveable(key = "chat") {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val showEmoji by remember {
        mutableStateOf(false)
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true
        ) {
            if(!indexViewModel.webSocketConnected){
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LottieAnimation(
                                modifier = Modifier.size(150.dp),
                                composition = composition
                            )
                            Text(text = "已断开和聊天服务器的连接，请尝试重启APP", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(indexViewModel.chatHistory.reversed()) {
                ChatItem(
                    chatMessage = it,
                    navController = navController,
                    self = it.userId == indexViewModel.self.id
                )
            }
        }
        Card(elevation = 4.dp) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(text = "请文明用语哦")
                    }
                )
                IconButton(onClick = {
                    indexViewModel.sendMessage(text)
                    text = ""
                    focusManager.clearFocus()
                }) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
        AnimatedVisibility(visible = showEmoji) {
            Box(modifier = Modifier.fillMaxWidth()){
                LazyVerticalGrid(cells = GridCells.Adaptive(10.dp),modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    for (emoji in EMOJI_LIST) {
                        item {
                            Box(modifier = Modifier.padding(4.dp), contentAlignment = Alignment.Center){
                                Text(text = emoji.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun ChatItem(navController: NavController, chatMessage: ChatMessage, self: Boolean) {
    if (self) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Nickname
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(chatMessage.username, fontSize = 13.sp)
                }

                // Message
                Card(
                    elevation = 2.dp
                ) {
                    SelectionContainer(Modifier.padding(8.dp)) {
                        // SmartLinkText(text = chatMessage.message, maxLines = 10)
                        Text(text = chatMessage.message)
                    }
                }

                // Preview
                chatMessage.message.parseUrls().find { it.isImage() }?.let {
                    val painter = rememberImagePainter(it.text)
                    if(painter.state !is ImagePainter.State.Error) {
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
                .padding(8.dp), horizontalArrangement = Arrangement.Start
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
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(chatMessage.username, fontSize = 13.sp)
                }
                Card(
                    elevation = 2.dp
                ) {
                    SelectionContainer(Modifier.padding(8.dp)) {
                        SmartLinkText(text = chatMessage.message, maxLines = 10)
                    }
                }
                // Preview
                chatMessage.message.parseUrls().find { it.isImage() }?.let {
                    val painter = rememberImagePainter(it.text)
                    if(painter.state !is ImagePainter.State.Error) {
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

data class ChatMessage(
    val username: String,
    val userId: String,
    val avatar: String,
    val message: String,
    val timestamp: Long
)
