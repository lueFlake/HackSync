package com.hacksync.general.commands.status

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReadKanbanStatusCommand(
    @Contextual
    val id: UUID
)