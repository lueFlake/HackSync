package com.hacksync.general.entities;

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class User(@Contextual
                override val id: UUID,
                val email: String,
                val passwordHash: String,
                val role: Role,
                val name: String,
                val avatarUrl: String ?= null,
                @Contextual
                val createdAt: Instant,
                @Contextual
                val updatedAt: Instant
) : IEntity