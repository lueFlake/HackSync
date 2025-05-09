package com.hacksync.general.entities

import com.hacksync.general.dto.HackathonDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

data class Hackathon(
    val id: UUID,
    val description: String?,
    val dateOfRegister: Instant?,
    val dateOfStart: Instant?,
    val dateOfEnd: Instant?,
    val name: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val deadlines: List<Deadline> = emptyList()
) {
    fun toDto() = HackathonDto(
        id = id,
        name = name,
        description = description,
        dateOfRegister = dateOfRegister,
        dateOfStart = dateOfStart,
        dateOfEnd = dateOfEnd,
        deadlines = deadlines.map { it.toDto() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 