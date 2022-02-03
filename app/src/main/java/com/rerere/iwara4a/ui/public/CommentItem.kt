package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.model.comment.Comment
import com.rerere.iwara4a.model.comment.CommentPosterType
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable
import com.rerere.iwara4a.util.setClipboard

@Composable
fun CommentItem(
    navController: NavController,
    comment: Comment,
    onReply: (Comment) -> Unit,
    parent: Boolean = true
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (parent) {
                    it.border(BorderStroke(0.1.dp, Color.Gray.copy(alpha = 0.3f)))
                } else {
                    it
                }
            }
            .padding(8.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    val painter = rememberImagePainter(comment.authorPic)
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                navController.navigate("user/${comment.authorId}")
                            }
                            .placeholder(
                                visible = painter.state is ImagePainter.State.Loading,
                                highlight = PlaceholderHighlight.shimmer()
                            ),
                        painter = painter,
                        contentDescription = null
                    )
                }
                Column(Modifier.padding(horizontal = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier
                                .padding(end = 8.dp)
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
                        }
                    }
                    Text(text = comment.date, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(modifier = Modifier
                .combinedClickable(
                    onClick = {
                        onReply.invoke(comment)
                    },
                    onLongClick = {
                        context.setClipboard(comment.content)
                    }
                )
                .padding(horizontal = 4.dp), text = comment.content)
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                comment.reply.forEach {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .background(
                                color = Color.Gray.copy(0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        CommentItem(navController, it, onReply, false)
                    }
                }
            }
        }
    }
}