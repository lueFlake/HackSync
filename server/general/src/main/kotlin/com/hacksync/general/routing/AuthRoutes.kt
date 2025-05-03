package com.hacksync.general.routing

import com.hacksync.general.commands.ReadUserByEmailCommand
import com.hacksync.general.dtos.AuthResponse
import com.hacksync.general.dtos.LoginRequest
import com.hacksync.general.dtos.RegisterRequest
import com.hacksync.general.services.AuthService
import com.hacksync.general.services.UserService
import com.hacksync.general.docs.AuthDocs
import com.hacksync.general.utils.jsonBody
import io.ktor.http.*
import io.ktor.server.application.*
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
                AuthResponse(
                    token = authService.login(LoginRequest(request.email, request.password)),
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
            val token = authService.login(request)
            val user = userService.getByEmail(ReadUserByEmailCommand(request.email))
            
            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    token = token,
                    userId = user.id.toString(),
                    email = user.email,
                    name = user.name
                )
            )
        }
/*
        post("/refresh", AuthDocs.refreshToken) {
            val authService = call.scope.get<AuthService>()
            val command = call.receive<RefreshTokenCommand>()
            val tokens = authService.refreshToken(command)
            call.respond(HttpStatusCode.OK, tokens)
        }

        post("/logout", AuthDocs.logout) {
            val authService = call.scope.get<AuthService>()
            val refreshToken = call.request.header("Authorization")?.removePrefix("Bearer ")
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "No refresh token provided")

            authService.logout(refreshToken)
            call.respond(HttpStatusCode.OK)
        }

        get("/validate", AuthDocs.validateToken) {
            val authService = call.scope.get<AuthService>()
            val accessToken = call.request.header("Authorization")?.removePrefix("Bearer ")
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "No access token provided")

            if (authService.validateToken(accessToken)) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid or expired token")
            }
        }*/
    }
} 