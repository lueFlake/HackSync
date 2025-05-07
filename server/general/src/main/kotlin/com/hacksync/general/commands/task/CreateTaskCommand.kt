package com.hacksync.general.commands.task

import com.hacksync.general.entities.Priority
import com.hacksync.general.entities.Task
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class CreateTaskCommand(
    val number: String,
    val name: String,
    val description: String,
    val priority: Priority,
    @Contextual
    val linkId: UUID?,
    @Contextual
    val userId: UUID?,
    @Contextual
    val dueDate: Instant?
) {
    companion object {
        val DEFAULT_BACKLOG_STATUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }

    fun execute(): Task {
        val now = Instant.now()
        return Task(
            id = UUID.randomUUID(),
            number = number,
            name = name,
            description = description,
            priority = priority,
            linkId = linkId,
            userId = userId,
            status = null,
            dueDate = dueDate,
            createdAt = now,
            updatedAt = now
        )
    }
} 