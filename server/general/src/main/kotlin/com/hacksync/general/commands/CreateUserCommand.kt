package com.hacksync.general.commands

import com.hacksync.general.entities.Role
import com.hacksync.general.entities.User
import com.hacksync.general.utils.PasswordHashing
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class CreateUserCommand(
    val email: String,
    val password: String,
    val role: String,
    val name: String,
    val avatarUrl: String? = null
) {
    fun execute(): User {
        return User(
            id = UUID.randomUUID(),
            email = this@CreateUserCommand.email,
            passwordHash = PasswordHashing.hash(this@CreateUserCommand.password),
            role = Role.validate(this@CreateUserCommand.role),
            name = this@CreateUserCommand.name,
            avatarUrl = this@CreateUserCommand.avatarUrl,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}