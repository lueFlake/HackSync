package com.hacksync.general.commands.hackathon

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class UpdateHackathonCommand(
    @Contextual
    val id: UUID,
    val name: String? = null,
    val description: String? = null,
    val dateOfRegister: String? = null,
    val dateOfStart: String? = null,
    val dateOfEnd: String? = null,
    val extraDestfine: String? = null
) {
    fun toInstant(date: String?): Instant? = date?.let { Instant.parse(it) }
} 