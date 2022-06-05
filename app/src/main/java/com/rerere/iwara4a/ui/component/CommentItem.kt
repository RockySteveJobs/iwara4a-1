package com.rerere.iwara4a.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import coil.compose.rememberAsyncImagePainter
import com.rerere.iwara4a.data.model.comment.Comment
import com.rerere.iwara4a.data.model.comment.CommentPosterType
import com.rerere.iwara4a.data.model.comment.getAllReplies
import com.rerere.iwara4a.ui.component.modifier.noRippleClickable
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.setClipboard
import kotlinx.coroutines.launch

@Composable
fun CommentItem(
    comment: Comment,
    onRequestTranslate: suspend (String) -> String = { it },
    onReply: (Comment) -> Unit
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var content by remember {
        mutableStateOf(comment.content)
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            onReply(comment)
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(model = comment.authorPic)
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .noRippleClickable {
                            navController.navigate("user/${comment.authorId}")
                        },
                    painter = painter,
                    contentDescription = null
                )

                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .noRippleClickable {
                                    navController.navigate("user/${comment.authorId}")
                                },
                            text = comment.authorName,
                            fontSize = 17.sp
                        )
                        when (comment.posterType) {
                            CommentPosterType.OWNER -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PINK)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "UP主", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            CommentPosterType.SELF -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Yellow)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "你", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            else -> {}
                        }
                        if (comment.fromIwara4a) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Iwara4a",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Text(text = comment.date, fontSize = 12.sp)
                }
            }
            Box {
                var expandDropDown by remember {
                    mutableStateOf(false)
                }
                DropdownMenu(
                    expanded = expandDropDown,
                    onDismissRequest = { expandDropDown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("复制内容") },
                        onClick = {
                            context.setClipboard(comment.content)
                            expandDropDown = false
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.ContentCopy, null)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("翻译") },
                        onClick = {
                            scope.launch {
                                content = onRequestTranslate(content)
                                expandDropDown = false
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Translate, null)
                        }
                    )
                }
                Text(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                onReply.invoke(comment)
                            },
                            onLongClick = {
                                expandDropDown = true
                            }
                        )
                        .padding(horizontal = 4.dp),
                    text = content
                )
            }

            // 回复的回复
            val allReplies = comment.getAllReplies()
            var expandReplies by remember { mutableStateOf(allReplies.size <= 1) }
            if (allReplies.isNotEmpty()) {
                Crossfade(expandReplies) {
                    if (!it) {
                        TextButton(onClick = { expandReplies = true }) {
                            Text(
                                text = "共有${allReplies.size}条回复"
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            comment.reply.fastForEach { reply ->
                                ReplyItem(reply, onRequestTranslate, onReply)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReplyItem(
    comment: Comment,
    onRequestTranslate: suspend (String) -> String = { it },
    onReply: (Comment) -> Unit
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    var content by remember {
        mutableStateOf(comment.content)
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            onReply(comment)
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(model = comment.authorPic)
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .noRippleClickable {
                            navController.navigate("user/${comment.authorId}")
                        },
                    painter = painter,
                    contentDescription = null
                )

                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .noRippleClickable {
                                    navController.navigate("user/${comment.authorId}")
                                },
                            text = comment.authorName,
                            fontSize = 17.sp
                        )
                        when (comment.posterType) {
                            CommentPosterType.OWNER -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PINK)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "UP主", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            CommentPosterType.SELF -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Yellow)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "你", color = Color.Black, fontSize = 12.sp)
                                }
                            }
                            else -> {}
                        }
                        if (comment.fromIwara4a) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Iwara4a",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Text(text = comment.date, fontSize = 12.sp)
                }
            }
            Box {
                var expandDropDown by remember {
                    mutableStateOf(false)
                }
                DropdownMenu(
                    expanded = expandDropDown,
                    onDismissRequest = { expandDropDown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("复制内容") },
                        onClick = {
                            context.setClipboard(comment.content)
                            expandDropDown = false
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.ContentCopy, null)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("翻译") },
                        onClick = {
                            scope.launch {
                                content = onRequestTranslate(content)
                                expandDropDown = false
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Translate, null)
                        }
                    )
                }
                Text(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                onReply.invoke(comment)
                            },
                            onLongClick = {
                                expandDropDown = true
                            }
                        )
                        .padding(horizontal = 4.dp),
                    text = content
                )
            }

            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                comment.reply.fastForEach { reply ->
                    ReplyItem(reply, onRequestTranslate, onReply)
                }
            }
        }
    }
}