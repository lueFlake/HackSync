package com.hacksync.general.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class KanbanStatusDto(
    @Contextual
    val id: UUID,
    @Contextual
    val nextId: UUID?,
    val name: String,
    val color: String,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant
) : BaseDto 