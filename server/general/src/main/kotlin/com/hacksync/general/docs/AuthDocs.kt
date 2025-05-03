package com.hacksync.general.docs

import com.hacksync.general.dtos.LoginRequest
import com.hacksync.general.dtos.RegisterRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*

object AuthDocs {
    val registerUser: RouteConfig.() -> Unit = {
        operationId = "registerUser"
        summary = "Register a new user"
        description = "Creates a new user account and returns authentication token"
        tags = listOf("Auth")

        request {
            jsonBody<RegisterRequest> {
                example("Register User") {
                    value = RegisterRequest(
                        email = "user@example.com",
                        name = "John Doe",
                        password = "password123"
                    )
                    description = "Register a new user with email, name and password"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "User registered successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.Conflict to {
                description = "Email already exists"
            }
        }
    }

    val loginUser: RouteConfig.() -> Unit = {
        operationId = "loginUser"
        summary = "Login user"
        description = "Authenticates a user and returns their authentication token"
        tags = listOf("Auth")

        request {
            jsonBody<LoginRequest> {
                example("Login User") {
                    value = LoginRequest(
                        email = "user@example.com",
                        password = "password123"
                    )
                    description = "Login with email and password"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Login successful"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.Unauthorized to {
                description = "Invalid credentials"
            }
        }
    }

    val logout: RouteConfig.() -> Unit = {
        operationId = "logout"
        summary = "User logout"
        description = "Invalidates the current refresh token"
        tags = listOf("Authentication")

        response {
            HttpStatusCode.OK to {
                description = "Logout successful"
            }
            HttpStatusCode.Unauthorized to {
                description = "Invalid or expired token"
            }
        }
    }

    val validateToken: RouteConfig.() -> Unit = {
        operationId = "validateToken"
        summary = "Validate access token"
        description = "Validates the current access token"
        tags = listOf("Authentication")

        response {
            HttpStatusCode.OK to {
                description = "Token is valid"
            }
            HttpStatusCode.Unauthorized to {
                description = "Invalid or expired token"
            }
        }
    }
} 