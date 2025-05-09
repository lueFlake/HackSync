package com.hacksync.general.entities

import kotlinx.serialization.Contextual
import java.time.LocalDateTime
import java.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ChatMessage(
    val sender: String,
    val content: JsonElement,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT,
    val hackathonId: String? = null
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