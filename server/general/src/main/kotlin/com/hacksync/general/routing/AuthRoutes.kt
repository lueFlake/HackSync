package com.hacksync.general.routing

import com.hacksync.general.commands.user.ReadUserByEmailCommand
import com.hacksync.general.dto.*
import com.hacksync.general.services.AuthService
import com.hacksync.general.services.UserService
import com.hacksync.general.docs.AuthDocs
import com.hacksync.general.models.UserResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.get

fun Route.authRoutes() {
    route("/auth") {
        post("/register", AuthDocs.registerUser) {
            val authService = call.scope.get<AuthService>()
            val request = call.receive<RegisterRequest>()
            val user = authService.register(request)

            call.respond(
                HttpStatusCode.Created,
                UserResponse(
                    userId = user.id.toString(),
                    email = user.email,
                    name = user.name
                )
            )
        }

        post("/login", AuthDocs.loginUser) {
            val authService = call.scope.get<AuthService>()
            val userService = call.scope.get<UserService>()
            val request = call.receive<LoginRequest>()

            val tokens = authService.login(request)
            val user = userService.getByEmail(ReadUserByEmailCommand(request.email))

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    userId = user.id.toString(),
                    email = user.email,
                    name = user.name
                )
            )
        }

        post("/refresh", AuthDocs.refreshToken) {
            val authService = call.scope.get<AuthService>()
            val request = call.receive<RefreshTokenRequest>()

            val tokens = authService.refreshTokens(request)

            call.respond(
                HttpStatusCode.OK,
                TokenResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                )
            )
        }

        post("/logout", AuthDocs.logout) {
            val authService = call.scope.get<AuthService>()
            val refreshToken = call.request.header("Authorization")?.removePrefix("Bearer ")
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

            authService.logout(refreshToken)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Successfully logged out"))
        }

        get("/validate", AuthDocs.validateToken) {
            val authService = call.scope.get<AuthService>()
            val accessToken = call.request.header("Authorization")?.removePrefix("Bearer ")
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "No access token provided")

            try {
                authService.validateAccessToken(accessToken)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Token is valid"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid token"))
            }
        }

    }
}