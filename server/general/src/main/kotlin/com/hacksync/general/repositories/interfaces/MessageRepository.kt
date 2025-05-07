package com.hacksync.general.repositories.interfaces

import com.hacksync.general.model.ChatMessage

interface MessageRepository {
    fun save(message: ChatMessage)
    fun getAll(): List<ChatMessage>
} 