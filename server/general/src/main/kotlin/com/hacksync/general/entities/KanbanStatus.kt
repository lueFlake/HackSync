package com.hacksync.general.entities

import com.hacksync.general.dto.KanbanStatusDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class KanbanStatus(
    @Contextual
    override val id: UUID,
    @Contextual
    val nextId: UUID?,
    val name: String,
    val color: String,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant
) : IEntity {
    fun toDto() = KanbanStatusDto(
        id = id,
        nextId = nextId,
        name = name,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 