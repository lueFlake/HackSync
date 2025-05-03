package com.hacksync.general.entities

import java.time.Instant
import java.util.*

data class Task(override val id: UUID,
                val number: String,
                val name: String,
                val description: String,
                val priority: Priority,
                val linkId: UUID?,
                val userId: UUID?,
                val status: KanbanStatus,
                val dueDate: Instant?,
                val createdAt: Instant,
                val updatedAt: Instant
) : IEntity