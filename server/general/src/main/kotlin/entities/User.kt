import entities.Role
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(val id: Int = 0,
                val email: String,
                val passwordHash: String ?= null,
                val role: Role,
                val name: String,
                val avatarUrl: String ?= null,
                @Contextual
                val createdAt: LocalDateTime = LocalDateTime.now(),
                @Contextual
                val updatedAt: LocalDateTime = LocalDateTime.now())