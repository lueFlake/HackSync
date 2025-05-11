package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.ChatMessage
import java.util.UUID

interface MessageRepository {
    fun save(message: ChatMessage)
    fun getAll(): List<ChatMessage>
    fun findByHackathonId(hackathonId: UUID): List<ChatMessage>
} 