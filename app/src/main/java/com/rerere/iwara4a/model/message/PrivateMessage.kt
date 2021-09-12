package com.rerere.iwara4a.model.message

// 代表一个对话预览
// 为 https://ecchi.iwara.tv/messages 页面的消息Item
data class PrivateMessagePreview(
    val conversationId: Int,
    val title: String,
    val targetName: String,
    val targetId: String,
    val lastUpdated: String,
    val messages: Int
)

// 代表私聊对话列表
typealias PrivateMessagePreviewList = List<PrivateMessagePreview>

