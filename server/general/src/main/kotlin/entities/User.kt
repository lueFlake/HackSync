package entities;

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class User(@Contextual
                override val id: UUID,
                val email: String,
                val passwordHash: String ?= null,
                val role: Role,
                val name: String,
                val avatarUrl: String ?= null,
                @Contextual
                val createdAt: LocalDateTime = LocalDateTime.now(),
                @Contextual
                val updatedAt: LocalDateTime = LocalDateTime.now()) : IEntity