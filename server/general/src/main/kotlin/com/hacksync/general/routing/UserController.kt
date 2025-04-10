package com.hacksync.general.routing

import com.hacksync.general.commands.*
import com.hacksync.general.entities.User
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import com.hacksync.general.services.UserService
import java.util.*

fun Route.addUserRoutes() {
    route("/users") {

        post {
            val user = call.receive<CreateUserCommand>()
            val userId = call.scope.get<UserService>().create(user)
            call.respond(HttpStatusCode.Created, userId)
        }

        get("/{id}") {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val user = call.scope.get<UserService>().read(ReadUserCommand(id))
            call.respond(HttpStatusCode.OK, user)
        }

        get("/all") {
            call.respond(call.scope.get<UserService>().getAll())
        }

        /*@Operation(summary = "Update user")
        @RequestBody(UpdateUserCommand::class)
        @ApiResponse(responseCode = "200", description = "User updated")
        */put {
            call.scope.get<UserService>().update(call.receive<UpdateUserCommand>())
            call.respond(HttpStatusCode.OK)
        }

        patch<User>("/change_password") {
            call.scope.get<UserService>().changePassword(call.receive<ChangePasswordCommand>());
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            call.scope.get<UserService>().delete(DeleteUserCommand(UUID.fromString(call.parameters["id"]!!)))
            call.respond(HttpStatusCode.OK)
        }
    }
}