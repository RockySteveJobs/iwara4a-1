package com.rerere.iwara4a.ui.public

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@ExperimentalAnimationApi
@Composable
fun ReplyDialog(replyDialogState: ReplyDialogState) {
    val context = LocalContext.current
    AnimatedVisibility(visible = replyDialogState.visible) {
        Dialog(onDismissRequest = {
            replyDialogState.hide()
        }) {
            Surface(
                modifier = Modifier.padding(8.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    Modifier
                        .width(400.dp)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "回复 ${replyDialogState.author}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        value = replyDialogState.content,
                        onValueChange = {
                            replyDialogState.content = it
                        },
                        placeholder = {
                            Text(text = "文明用语哦！")
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        Toast.makeText(context, "还没写!🥰😅 这玩意参数太多了，好麻烦，有没有大佬写一下", Toast.LENGTH_SHORT).show()
                    }) {
                        Text(text = "回复")
                    }
                }
            }
        }
    }
}

@Stable
class ReplyDialogState(
    visible: Boolean,
    val author: String,
    val nid: Int,
    val replyTo: Int?,
) {
    var visible by mutableStateOf(visible)
    var content by mutableStateOf("")

    fun show() {
        visible = true
    }

    fun hide() {
        visible = false
    }
}

@Composable
fun rememberReplyDialogState(
    author: String,
    nid: Int,
    replyTo: Int?
): ReplyDialogState = remember {
    ReplyDialogState(
        visible = false,
        author = author,
        nid = nid,
        replyTo = replyTo
    )
}