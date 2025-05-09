package com.hacksync.general.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class HackathonDto(
    @Contextual
    val id: UUID,
    val name: String?,
    val description: String?,
    @Contextual
    val dateOfRegister: Instant?,
    @Contextual
    val dateOfStart: Instant?,
    @Contextual
    val dateOfEnd: Instant?,
    val deadlines: List<DeadlineDto> = emptyList(),
    @Contextual
    val createdAt: Instant?,
    @Contextual
    val updatedAt: Instant?
) : BaseDto