package com.hacksync.general.commands.task

import com.hacksync.general.entities.Priority
import com.hacksync.general.entities.Task
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class CreateTaskCommand(
    val name: String,
    val description: String,
    val priority: Priority,
    @Contextual
    val userId: UUID?,
    @Contextual
    val hackathonId: UUID?,
    @Contextual
    val dueDate: Instant?
) {
    companion object {
        val DEFAULT_BACKLOG_STATUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }

    fun execute(status: UUID): Task {
        val now = Instant.now()
        return Task(
            id = UUID.randomUUID(),
            number = "",
            serial = 0,
            name = name,
            description = description,
            priority = priority,
            linkId = null,
            userId = userId,
            status = status,
            hackathonId = hackathonId,
            dueDate = dueDate,
            createdAt = now,
            updatedAt = now
        )
    }
} 