package com.rerere.iwara4a.ui.screen.index.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.navigation.animation.AnimatedComposeNavigator
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.SmartLinkText
import com.rerere.iwara4a.ui.public.parseUrls
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable
import kotlin.math.PI

val EMOJI_LIST = "".toCharArray().toList()

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalCoilApi
@Composable
fun IRCPage(navController: NavController, indexViewModel: IndexViewModel) {
    var text by rememberSaveable(key = "chat") {
        mutableStateOf("")
    }
    val context = LocalContext.current
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
            if (!indexViewModel.webSocketConnected) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
                        Column(modifier = Modifier.noRippleClickable {
                            indexViewModel.reconnect()
                        }, horizontalAlignment = Alignment.CenterHorizontally) {
                            LottieAnimation(
                                modifier = Modifier.size(150.dp),
                                composition = composition,
                                iterations = LottieConstants.IterateForever
                            )
                            Text(text = "已断开和聊天服务器的连接，点击重连", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(40.dp))
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
                    if (text.isNotBlank()) {
                        indexViewModel.sendMessage(text)
                        text = ""
                        focusManager.clearFocus()
                    } else {
                        Toast.makeText(context, "消息不能为空!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Default.Send, null)
                }
            }
        }
        AnimatedVisibility(visible = showEmoji) {
            Box(modifier = Modifier.fillMaxWidth()) {
                LazyVerticalGrid(
                    cells = GridCells.Adaptive(10.dp), modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    for (emoji in EMOJI_LIST) {
                        item {
                            Box(
                                modifier = Modifier.padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
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
                    .padding(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Nickname
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp)) {
                    if (chatMessage.developer) {
                        Text(
                            text = "开发者",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(1.dp)
                                .background(color = PINK, shape = RoundedCornerShape(3.dp))
                                .padding(1.dp),
                        )
                    }
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            chatMessage.username,
                            fontSize = 13.sp,
                            style = LocalTextStyle.current.let {
                                if (chatMessage.developer) {
                                    it.copy(color = PINK, fontWeight = FontWeight.Bold)
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
                    SmartLinkText(text = chatMessage.message, maxLines = 10)
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp)) {
                    if (chatMessage.developer) {
                        Text(
                            text = "开发者",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(1.dp)
                                .background(color = PINK, shape = RoundedCornerShape(3.dp))
                                .padding(1.dp),
                        )
                    }
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            chatMessage.username,
                            fontSize = 13.sp,
                            style = LocalTextStyle.current.let {
                                if (chatMessage.developer) {
                                    it.copy(color = PINK, fontWeight = FontWeight.Bold)
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
                    SmartLinkText(text = chatMessage.message, maxLines = 10)
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

data class ChatMessage(
    val username: String,
    val userId: String,
    val avatar: String,
    val message: String,
    val timestamp: Long
) {
    val developer: Boolean
        get() = userId == "%E3%81%93%E3%81%93%E3%82%8D%E3%81%AA%E3%81%97RE"
}