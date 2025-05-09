package com.hacksync.general.entities

import com.hacksync.general.dto.TaskDto
import java.time.Instant
import java.util.*

data class Task(override val id: UUID,
                val serial: Long,
                val number: String,
                val name: String,
                val description: String,
                val priority: Priority,
                val linkId: UUID?,
                val userId: UUID?,
                val status: UUID?,
                val hackathonId: UUID?,
                val dueDate: Instant?,
                val createdAt: Instant,
                val updatedAt: Instant
) : IEntity {
    fun toDto() = TaskDto(
        id = id,
        number = number,
        name = name,
        description = description,
        priority = priority,
        linkId = linkId,
        userId = userId,
        status = status,
        hackathonId = hackathonId,
        dueDate = dueDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}