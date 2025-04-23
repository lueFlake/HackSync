package com.hacksync.general.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.hacksync.general.dtos.LoginRequest
import com.hacksync.general.dtos.RegisterRequest
import com.hacksync.general.entities.Role
import com.hacksync.general.entities.User
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.utils.PasswordUtils
import io.ktor.server.config.*
import java.time.Instant
import java.util.*

class AuthService(
    private val userRepository: JdbiUserRepository,
    private val config: ApplicationConfig
) {
    private val secret = config.property("jwt.secret").getString()
    private val issuer = config.property("jwt.issuer").getString()
    private val audience = config.property("jwt.audience").getString()

    suspend fun register(request: RegisterRequest): User {
        if (userRepository.getByEmail(request.email) != null) {
            throw Exception("User with this email already exists")
        }

        val hashedPassword = PasswordUtils.hashPassword(request.password)
        val user = User(
            id = UUID.randomUUID(),
            email = request.email,
            passwordHash = hashedPassword,
            role = Role.PARTICIPANT,
            name = request.name,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        userRepository.insert(user)
        return user
    }

    suspend fun login(request: LoginRequest): String {
        val user: User = userRepository.getByEmail(request.email)
            ?: throw Exception("Invalid email or password")

        if (!PasswordUtils.verifyPassword(request.password, user.passwordHash)) {
            throw Exception("Invalid email or password")
        }

        return generateToken(user)
    }

    private fun generateToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24h
            .sign(Algorithm.HMAC256(secret))
    }
} 