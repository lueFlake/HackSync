package com.hacksync.general.entities

import com.hacksync.general.dto.TeamDto
import java.time.Instant
import java.util.UUID

data class Team(
    val id: UUID,
    val linkId: UUID?,
    val name: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun toDto() = TeamDto(
        id = id,
        linkId = linkId,
        name = name
    )
} 