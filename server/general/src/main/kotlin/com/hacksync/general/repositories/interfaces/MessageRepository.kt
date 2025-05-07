package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.ChatMessage

interface MessageRepository {
    fun save(message: ChatMessage)
    fun getAll(): List<ChatMessage>
} 