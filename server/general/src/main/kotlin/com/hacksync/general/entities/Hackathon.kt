package com.hacksync.general.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class Hackathon(
    @Contextual
    val id: UUID,
    val description: String?,
    @Contextual
    val dateOfRegister: Instant?,
    @Contextual
    val dateOfStart: Instant?,
    @Contextual
    val dateOfEnd: Instant?,
    val extraDestfine: String?,
    @Contextual
    val linkId: UUID?,
    val name: String?
) 