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
        val DEFAULT_BACKLOG_STATUS_ID = UUID.fromString("11111111-1111-1111-1111-111111111111")
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
            status = null, // This will be set by the service
            dueDate = dueDate,
            createdAt = now,
            updatedAt = now
        )
    }
} 