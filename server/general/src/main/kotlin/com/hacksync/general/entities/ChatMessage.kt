package com.hacksync.general.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ChatMessage(
    val sender: String,
    val content: JsonElement, // Can be String or MediaContent
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT
)

@Serializable
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE
}

@Serializable
data class MediaContent(
    val url: String,
    val mimeType: String,
    val fileName: String,
    val fileSize: Long,
    val thumbnailUrl: String? = null
) 