package com.hacksync.general.repositories

import com.hacksync.general.model.ChatMessage
import com.hacksync.general.repositories.interfaces.MessageRepository

class InMemoryMessageRepository : MessageRepository {
    private val messages = mutableListOf<ChatMessage>()
    override fun save(message: ChatMessage) {
        messages.add(message)
    }
    override fun getAll(): List<ChatMessage> = messages.toList()
} 