package com.hacksync.general.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class DeadlineDto(
    @Contextual
    override val id: UUID,
    @Contextual
    val date: Instant?,
    @Contextual
    val linkId: UUID?,
    val name: String?,
    val kind: String?
) : BaseDto 