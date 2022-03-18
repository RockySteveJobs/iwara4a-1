package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rerere.iwara4a.model.comment.Comment
import com.rerere.iwara4a.model.comment.CommentPosterType
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.util.setClipboard

@Composable
fun CommentItem(
    navController: NavController,
    comment: Comment,
    onReply: (Comment) -> Unit
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .noRippleClickable {
                            navController.navigate("user/${comment.authorId}")
                        },
                    model = comment.authorPic,
                    contentDescription = null
                )

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
                            else -> {}
                        }
                    }
                    Text(text = comment.date, fontSize = 12.sp)
                }
            }
            Text(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            onReply.invoke(comment)
                        },
                        onLongClick = {
                            context.setClipboard(comment.content)
                        }
                    )
                    .padding(horizontal = 4.dp),
                text = comment.content
            )
            Column(
                Modifier.fillMaxWidth()
            ) {
                comment.reply.forEach {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                    ) {
                        CommentItem(navController, it, onReply)
                    }
                }
            }
        }
    }
}