package com.hacksync.general.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TeamDto(
    @Contextual
    val id: UUID,
    @Contextual
    val linkId: UUID?,
    val name: String?
) : BaseDto 