package com.rerere.iwara4a.ui.component

import androidx.compose.runtime.*
import com.rerere.iwara4a.model.comment.CommentPostParam

@Composable
fun rememberReplyDialogState() = remember {
    ReplyDialogState()
}

@Stable
class ReplyDialogState {
    var replyTo by mutableStateOf("")
    var nid by mutableStateOf(0)
    var commentId by mutableStateOf(-1)
    var commentPostParam by mutableStateOf(CommentPostParam.Default)

    var content: String by mutableStateOf("")

    var posting by mutableStateOf(false)

    var showDialog by mutableStateOf(false)

    fun open(
        replyTo: String,
        nid: Int,
        commentId: Int?,
        commentPostParam: CommentPostParam
    ) {
        this.replyTo = replyTo
        this.nid = nid
        this.commentId = commentId ?: -1
        this.commentPostParam = commentPostParam

        this.showDialog = true
    }
}