package com.hacksync.general.entities

import com.hacksync.general.dto.DeadlineDto
import java.time.Instant
import java.util.UUID

data class Deadline(
    val id: UUID,
    val date: Instant?,
    val linkId: UUID?,
    val name: String?,
    val type: String?
) {
    fun toDto() = DeadlineDto(
        id = id,
        date = date,
        linkId = linkId,
        name = name,
        kind = type
    )
} 