package com.hacksync.general.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val accessToken : String,
    val refreshToken : String,
    val userId: String,
    val email: String,
    val name: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)