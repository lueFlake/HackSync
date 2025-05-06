package com.hacksync.general.commands.task

import com.hacksync.general.entities.Priority
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class UpdateTaskCommand(
    @Contextual
    val id: UUID,
    val number: String? = null,
    val name: String? = null,
    val description: String? = null,
    val priority: Priority? = null,
    @Contextual
    val linkId: UUID? = null,
    @Contextual
    val userId: UUID? = null,
    @Contextual
    val statusId: UUID? = null,
    @Contextual
    val dueDate: Instant? = null
) 