package com.hacksync.general.services

import com.hacksync.general.entities.ChatMessage
import com.hacksync.general.entities.MediaContent
import com.hacksync.general.entities.MessageType
import com.hacksync.general.repositories.interfaces.MessageRepository
import com.hacksync.general.routing.Connection
import io.ktor.websocket.*
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val logger = LoggerFactory.getLogger("ChatService")

class ChatService(private val messageRepository: MessageRepository) {
    private val connections = ConcurrentHashMap<UUID, MutableSet<Connection>>()

    fun addConnection(connection: Connection) {
        connections.computeIfAbsent(connection.hackathonId) { mutableSetOf() }.add(connection)
        logger.info("New connection added for hackathon ${connection.hackathonId}. Total connections: ${connections[connection.hackathonId]?.size}")
    }

    fun removeConnection(connection: Connection) {
        connections[connection.hackathonId]?.remove(connection)
        logger.info("Connection removed for hackathon ${connection.hackathonId}. Remaining connections: ${connections[connection.hackathonId]?.size}")
    }

    fun processMessage(text: String, hackathonId: UUID): ChatMessage {
        val jsonElement = Json.parseToJsonElement(text)
        val message = when {
            jsonElement is JsonObject && jsonElement.containsKey("type") -> {
                val type = jsonElement["type"]?.jsonPrimitive?.content
                when (type) {
                    "TEXT" -> Json.decodeFromJsonElement<ChatMessage>(jsonElement)
                    "IMAGE", "VIDEO", "AUDIO", "FILE" -> {
                        // Parse content as MediaContent
                        val content = Json.decodeFromJsonElement<MediaContent>(
                            jsonElement["content"]?.jsonObject ?: throw IllegalArgumentException("Missing media content")
                        )
                        ChatMessage(
                            sender = jsonElement["sender"]?.jsonPrimitive?.content ?: "Unknown",
                            content = Json.encodeToJsonElement(content),
                            timestamp = System.currentTimeMillis(),
                            type = MessageType.valueOf(type),
                            hackathonId = hackathonId.toString()
                        )
                    }
                    else -> throw IllegalArgumentException("Unsupported message type: $type")
                }
            }
            else -> Json.decodeFromJsonElement<ChatMessage>(jsonElement)
        }

        // Validate message content based on type
        when (message.type) {
            MessageType.TEXT -> {
                if (message.content !is JsonPrimitive) {
                    throw IllegalArgumentException("Text messages must have string content")
                }
            }
            MessageType.IMAGE, MessageType.VIDEO, MessageType.AUDIO, MessageType.FILE -> {
                if (message.content !is JsonObject) {
                    throw IllegalArgumentException("Media messages must have MediaContent object")
                }
                val mediaContent = Json.decodeFromJsonElement<MediaContent>(message.content as JsonObject)
                //validateMediaContent(mediaContent)
            }
        }

        messageRepository.save(message)
        return message
    }

    suspend fun broadcastMessage(message: ChatMessage, hackathonId: UUID) {
        val messageJson = Json.encodeToString(ChatMessage.serializer(), message)
        connections[hackathonId]?.forEach { connection ->
            try {
                connection.session.send(messageJson)
            } catch (e: Exception) {
                logger.error("Error sending message to ${connection.name}: ${e.message}")
                removeConnection(connection)
            }
        }
    }

    fun getMessageHistory(hackathonId: UUID): List<ChatMessage> {
        return messageRepository.findByHackathonId(hackathonId)
    }

    private fun validateMediaContent(mediaContent: MediaContent) {
        when (getMessageTypeFromMime(mediaContent.mimeType)) {
            MessageType.IMAGE -> {
                if (!isValidImageUrl(mediaContent.url)) {
                    throw IllegalArgumentException("Invalid image URL format")
                }
            }
            MessageType.VIDEO -> {
                if (!isValidVideoUrl(mediaContent.url)) {
                    throw IllegalArgumentException("Invalid video URL format")
                }
            }
            MessageType.AUDIO -> {
                if (!isValidAudioUrl(mediaContent.url)) {
                    throw IllegalArgumentException("Invalid audio URL format")
                }
            }
            MessageType.FILE -> {
                if (!isValidFileUrl(mediaContent.url)) {
                    throw IllegalArgumentException("Invalid file URL format: ${mediaContent.url}")
                }
            }
            else -> throw IllegalArgumentException("Unsupported media type: ${mediaContent.mimeType}")
        }
    }

    private fun getMessageTypeFromMime(mimeType: String): MessageType = when {
        mimeType.startsWith("image/") -> MessageType.IMAGE
        mimeType.startsWith("video/") -> MessageType.VIDEO
        mimeType.startsWith("audio/") -> MessageType.AUDIO
        else -> MessageType.FILE
    }

    private fun isValidImageUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://") ||
               url.startsWith("data:image/")
    }

    private fun isValidVideoUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://") ||
               url.startsWith("data:video/")
    }

    private fun isValidAudioUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://") ||
               url.startsWith("data:audio/")
    }

    private fun isValidFileUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }
}
