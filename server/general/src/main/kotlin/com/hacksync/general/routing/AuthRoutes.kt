package com.hacksync.general.routing

import com.hacksync.general.commands.ReadUserByEmailCommand
import com.hacksync.general.dtos.AuthResponse
import com.hacksync.general.dtos.LoginRequest
import com.hacksync.general.dtos.RegisterRequest
import com.hacksync.general.services.AuthService
import com.hacksync.general.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.scope

fun Route.authRoutes() {
    route("/auth") {
        post("/register") {
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

        post("/login") {
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
    }
} 