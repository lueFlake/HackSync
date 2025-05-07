package com.hacksync.general.repositories

import com.hacksync.general.model.ChatMessage
import com.hacksync.general.model.MessageType
import com.hacksync.general.repositories.interfaces.MessageRepository
import org.jdbi.v3.core.Jdbi
import kotlinx.serialization.json.Json

class JdbiMessageRepository(private val jdbi: Jdbi) : MessageRepository {
    override fun save(message: ChatMessage) {
        jdbi.useHandle<Exception> { handle ->
            handle.createUpdate("INSERT INTO chat_messages (sender, content, type) VALUES (:sender, :content, :type)")
                .bind("sender", message.sender)
                .bind("content", message.content.toString())
                .bind("type", message.type.name)
                .execute()
        }
    }

    override fun getAll(): List<ChatMessage> {
        return jdbi.withHandle<List<ChatMessage>, Exception> { handle ->
            handle.createQuery("SELECT sender, content, type FROM chat_messages")
                .map { rs, _ ->
                    ChatMessage(
                        sender = rs.getString("sender"),
                        content = Json.parseToJsonElement(rs.getString("content")),
                        type = MessageType.valueOf(rs.getString("type"))
                    )
                }
                .list()
        }
    }
} 