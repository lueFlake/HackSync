package com.hacksync.general.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.hacksync.general.dto.*
import com.hacksync.general.entities.Role
import com.hacksync.general.entities.User
import com.hacksync.general.exceptions.ValidationException
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.repositories.JdbiRevokedTokenRepository
import com.hacksync.general.utils.PasswordUtils
import io.ktor.server.config.*
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Instant
import java.util.*

class AuthService(
    private val userRepository: JdbiUserRepository,
    private val revokedTokenRepository: JdbiRevokedTokenRepository,
    config: ApplicationConfig
) {
    private val secret = config.property("ktor.jwt.secret").getString()
    private val issuer = config.property("ktor.jwt.issuer").getString()
    private val audience = config.property("ktor.jwt.audience").getString()
    private val accessTokenExpiry = config.property("ktor.jwt.accessTokenExpiry").getString().toLong()
    private val refreshTokenExpiry = config.property("ktor.jwt.refreshTokenExpiry").getString().toLong()

    fun register(request: RegisterRequest): User {
        if (userRepository.getByEmail(request.email) != null) {
            throw ValidationException("User with this email already exists")
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

    suspend fun login(request: LoginRequest): TokenResponse {
        val user: User = userRepository.getByEmail(request.email)
            ?: throw ValidationException("Invalid email or password")

        if (!PasswordUtils.verifyPassword(request.password, user.passwordHash)) {
            throw ValidationException("Invalid email or password")
        }

        return generateTokens(user)
    }

    suspend fun refreshTokens(request: RefreshTokenRequest): TokenResponse {
        val decodedJWT = try {
            JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
                .verify(request.refreshToken)
                .also { decoded ->
                    if (decoded.getClaim("tokenType").asString() != "refresh") {
                        throw ValidationException("Not a refresh token")
                    }
                }
        } catch (ex: Exception) {
            throw ValidationException("Invalid refresh token")
        }

        if (revokedTokenRepository.isRevoked(hashToken(request.refreshToken))) {
            throw ValidationException("Refresh token was revoked")
        }

        val userId = decodedJWT.getClaim("userId").asString()
        val user = userRepository.getById(UUID.fromString(userId))
            ?: throw ValidationException("User not found")

        revokedTokenRepository.revoke(
            tokenHash = hashToken(request.refreshToken),
            expiresAt = decodedJWT.expiresAt.toInstant()
        )

        return generateTokens(user)
    }

    private fun hashToken(token: String): String {
        return md5(token);
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun generateTokens(user: User): TokenResponse {
        val accessToken = generateAccessToken(user)
        val refreshToken = generateRefreshToken(user)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    private fun generateAccessToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiry))
            .sign(Algorithm.HMAC256(secret))
    }

    private fun generateRefreshToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id.toString())
            .withClaim("tokenType", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiry))
            .sign(Algorithm.HMAC256(secret))
    }

    suspend fun logout(refreshToken: String) {
        val decodedJWT = try {
            JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build()
                .verify(refreshToken)
        } catch (ex: Exception) {
            throw ValidationException("Invalid or expired token")
        }

        revokedTokenRepository.revoke(
            tokenHash = hashToken(refreshToken),
            expiresAt = decodedJWT.expiresAt.toInstant()
        )
    }

    fun validateAccessToken(accessToken: String) {
        try {
            val verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .withAudience(audience)
                .build()

            val decodedJWT = verifier.verify(accessToken)

            val userId = decodedJWT.getClaim("userId").asString()
            userRepository.getById(UUID.fromString(userId)) ?: throw ValidationException("User not found")

            if (decodedJWT.expiresAt.before(Date())) {
                throw ValidationException("Token has expired")
            }

            if (decodedJWT.getClaim("tokenType")?.asString() == "refresh") {
                throw ValidationException("Invalid token type")
            }
        } catch (ex: Exception) {
            throw ValidationException("Invalid token")
        }
    }
} 