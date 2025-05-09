package com.hacksync.general.dto

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.entities.Priority
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class TaskDto(
    @Contextual
    val id: UUID,
    val number: String,
    val name: String,
    val description: String,
    val priority: Priority,
    @Contextual
    val linkId: UUID?,
    @Contextual
    val userId: UUID?,
    @Contextual
    val status: UUID?,
    @Contextual
    val hackathonId: UUID?,
    @Contextual
    val dueDate: Instant?,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant
) : BaseDto