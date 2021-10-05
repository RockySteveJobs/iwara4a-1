package com.rerere.iwara4a.ui.public

import androidx.compose.runtime.*
import com.rerere.iwara4a.model.comment.CommentPostParam
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Composable
fun rememberReplyDialogState() = remember {
    ReplyDialogState(
        MaterialDialogState()
    )
}

@Stable
class ReplyDialogState(
    val materialDialog: MaterialDialogState
) {
    var replyTo by mutableStateOf("")
    var nid by mutableStateOf(0)
    var commentId by mutableStateOf(-1)
    var commentPostParam by mutableStateOf(CommentPostParam.Default)

    var content: String by mutableStateOf("")

    var posting by mutableStateOf(false)

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

        materialDialog.show()
    }
}