package com.hacksync.general.routing

import com.hacksync.general.commands.user.ReadUserByEmailCommand
import com.hacksync.general.dto.*
import com.hacksync.general.services.AuthService
import com.hacksync.general.services.UserService
import com.hacksync.general.docs.AuthDocs
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.get
import io.ktor.server.sessions.*

fun Route.authRoutes() {
    route("/auth") {
        post("/register", AuthDocs.registerUser) {
            val authService = call.scope.get<AuthService>()
            val request = call.receive<RegisterRequest>()
            val user = authService.register(request)

            call.respond(
                HttpStatusCode.Created,
                UserDto(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                    role = user.role,
                    avatarUrl = user.avatarUrl,
                    createdAt = user.createdAt,
                    updatedAt = user.updatedAt
                )
            )
        }

        post("/login", AuthDocs.loginUser) {
            val authService = call.scope.get<AuthService>()
            val userService = call.scope.get<UserService>()
            val request = call.receive<LoginRequest>()

            val tokens = authService.login(request)
            val user = userService.getByEmail(ReadUserByEmailCommand(request.email))

            call.response.cookies.append(
                name = "refreshToken",
                value = tokens.refreshToken,
                secure = true,
                httpOnly = true,
                maxAge = 24 * 60 * 60,
                extensions = mapOf("SameSite" to "Lax")
            )

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = "",
                    userId = user.id.toString(),
                    email = user.email,
                    name = user.name
                )
            )
        }

        post("/refresh", AuthDocs.refreshToken) {
            val authService = call.scope.get<AuthService>()
            val refreshToken = call.request.cookies["refreshToken"]
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

            val tokens = authService.refreshTokens(RefreshTokenRequest(refreshToken))

            call.response.cookies.append(
                name = "refreshToken",
                value = tokens.refreshToken,
                secure = true,
                httpOnly = true,
                maxAge = 24 * 60 * 60,
                extensions = mapOf("SameSite" to "Lax")
            )

            call.respond(
                HttpStatusCode.OK,
                TokenResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = "",
                )
            )
        }

        post("/logout", AuthDocs.logout) {
            val authService = call.scope.get<AuthService>()
            val refreshToken = call.request.cookies["refreshToken"]
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

            authService.logout(refreshToken)

            call.response.cookies.append(
                name = "refreshToken",
                value = "",
                secure = true,
                httpOnly = true,
                maxAge = 0,
                extensions = mapOf("SameSite" to "Lax")
            )

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