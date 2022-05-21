package com.rerere.iwara4a.data.model.comment

data class CommentPostParam(
    val antiBotKey: String,
    val formId: String,
    val formBuildId: String,
    val formToken: String,
    val honeypotTime: String
) {
    companion object {
        val Default = CommentPostParam(
            "",
            "",
            "",
            "",
            ""
        )
    }
}
