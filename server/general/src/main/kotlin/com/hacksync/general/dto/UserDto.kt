package com.hacksync.general.dto

import com.hacksync.general.entities.Role
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class UserDto(
    @Contextual
    val id: UUID,
    val email: String,
    val role: Role,
    val name: String,
    val avatarUrl: String?,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant
) : BaseDto