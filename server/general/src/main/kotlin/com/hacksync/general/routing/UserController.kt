package com.hacksync.general.routing

import com.hacksync.general.commands.CreateUserCommand
import com.hacksync.general.commands.DeleteUserCommand
import com.hacksync.general.commands.ReadUserCommand
import com.hacksync.general.commands.UpdateUserCommand
import com.hacksync.general.entities.User
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import com.hacksync.general.services.UserService
import java.util.*/*\
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
*/
fun Route.addUserRoutes() {
    route("/users") {
        /*@Operation(summary = "Create user")
        @RequestBody(CreateUserCommand::class)
        @ApiResponse(responseCode = "201", description = "User created")
        */post {
            val user = call.receive<CreateUserCommand>()
            val userId = call.scope.get<UserService>().create(user)
            call.respond(HttpStatusCode.Created, userId)
        }

        /*@Operation(summary = "Get user by ID")
        @Parameter(name = "id", required = true)
        @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = User::class))])
        @ApiResponse(responseCode = "404", description = "User not found")
        */get("/{id}") {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val user = call.scope.get<UserService>().read(ReadUserCommand(id))
            call.respond(HttpStatusCode.OK, user)
        }

        /*@Operation(summary = "List all users")
        @ApiResponse(responseCode = "200", content = [Content(schema = Schema(implementation = Array<User>::class))])
        */get("/all") {
            call.respond(call.scope.get<UserService>().getAll())
        }

        /*@Operation(summary = "Update user")
        @RequestBody(UpdateUserCommand::class)
        @ApiResponse(responseCode = "200", description = "User updated")
        */put {
            call.scope.get<UserService>().update(call.receive<UpdateUserCommand>())
            call.respond(HttpStatusCode.OK)
        }

        /*@Operation(summary = "Delete user")
        @Parameter(name = "id", required = true)
        @ApiResponse(responseCode = "200", description = "User deleted")
        @ApiResponse(responseCode = "404", description = "User not found")
        */delete("/{id}") {
            call.scope.get<UserService>().delete(DeleteUserCommand(UUID.fromString(call.parameters["id"]!!)))
            call.respond(HttpStatusCode.OK)
        }
    }
}