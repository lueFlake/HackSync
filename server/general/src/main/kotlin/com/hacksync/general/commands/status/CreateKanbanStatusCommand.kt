package com.hacksync.general.commands.status

import com.hacksync.general.entities.KanbanStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class CreateKanbanStatusCommand(
    val name: String,
    val color: String,
    @Contextual
    val nextId: UUID? = null
) {
    fun execute(): KanbanStatus {
        return KanbanStatus(
            id = UUID.randomUUID(),
            name = this@CreateKanbanStatusCommand.name,
            color = this@CreateKanbanStatusCommand.color,
            nextId = this@CreateKanbanStatusCommand.nextId,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}