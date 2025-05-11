package com.hacksync.general.repositories

import com.hacksync.general.entities.ChatMessage
import com.hacksync.general.entities.MessageType
import com.hacksync.general.repositories.interfaces.MessageRepository
import org.jdbi.v3.core.Jdbi
import kotlinx.serialization.json.Json
import java.util.UUID

class JdbiMessageRepository(private val jdbi: Jdbi) : MessageRepository {
    override fun save(message: ChatMessage) {
        println(message.hackathonId)
        jdbi.useHandle<Exception> { handle ->
            handle.createUpdate("""
                INSERT INTO chat_messages (sender, content, type, timestamp, hackathon_id) 
                VALUES (:sender, :content, :type, :timestamp, :hackathonId)
            """)
                .bind("sender", message.sender)
                .bind("content", message.content.toString())
                .bind("type", message.type.name)
                .bind("timestamp", message.timestamp)
                .bind("hackathonId", UUID.fromString(message.hackathonId))
                .execute()
        }
    }

    override fun getAll(): List<ChatMessage> {
        return jdbi.withHandle<List<ChatMessage>, Exception> { handle ->
            handle.createQuery("SELECT sender, content, type, timestamp, hackathon_id FROM chat_messages")
                .map { rs, _ ->
                    ChatMessage(
                        sender = rs.getString("sender"),
                        content = Json.parseToJsonElement(rs.getString("content")),
                        type = MessageType.valueOf(rs.getString("type")),
                        timestamp = rs.getLong("timestamp"),
                        hackathonId = rs.getString("hackathon_id")
                    )
                }
                .list()
        }
    }

    override fun findByHackathonId(hackathonId: UUID): List<ChatMessage> {
        return jdbi.withHandle<List<ChatMessage>, Exception> { handle ->
            handle.createQuery("""
                SELECT sender, content, type, timestamp, hackathon_id 
                FROM chat_messages 
                WHERE hackathon_id = :hackathonId
                ORDER BY timestamp ASC
            """)
                .bind("hackathonId", hackathonId)
                .map { rs, _ ->
                    ChatMessage(
                        sender = rs.getString("sender"),
                        content = Json.parseToJsonElement(rs.getString("content")),
                        type = MessageType.valueOf(rs.getString("type")),
                        timestamp = rs.getLong("timestamp"),
                        hackathonId = rs.getString("hackathon_id")
                    )
                }
                .list()
        }
    }
} 