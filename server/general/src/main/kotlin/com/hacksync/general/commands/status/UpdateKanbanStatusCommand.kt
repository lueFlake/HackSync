package com.hacksync.general.commands.status

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UpdateKanbanStatusCommand(
    @Contextual
    val id: UUID,
    val name: String? = null,
    val color: String? = null,
    @Contextual
    val nextId: UUID? = null
) 