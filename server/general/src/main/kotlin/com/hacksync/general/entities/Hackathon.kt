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
    val extraDestfine: String?,
    val name: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?
) {
    fun toDto() = HackathonDto(
        id = id,
        name = name,
        description = description,
        dateOfRegister = dateOfRegister,
        dateOfStart = dateOfStart,
        dateOfEnd = dateOfEnd,
        extraDestfine = extraDestfine,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 