package routing

import entities.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.scope
import repositories.UserRepository
import services.UserService
import java.util.*

fun Route.addUserRoutes () {

    // Create user
    post("/users") {
        // Получаем данные пользователя из тела запроса
        val user = call.receive<User>()
        val userService = call.scope.get<UserService>()
        val userId = userService.create(user)
        // Возвращаем HTTP-статус 201 Created и ID созданного пользователя
        call.respond(HttpStatusCode.Created, userId)
    }

    // Read user
    get("/users/{id}") {
        val id = UUID.fromString(call.parameters["id"])
        val userService = call.scope.get<UserService>()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        try {
            val user = userService.read(id)
            call.respond(HttpStatusCode.OK, user)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "User not found: $e")
        }
    }

    // Update user
    put("/users/{id}") {
        val id = UUID.fromString(call.parameters["id"])
        val user = call.receive<User>()
        val userService = call.scope.get<UserService>()
        try {
            userService.update(id, user)
            call.respond(HttpStatusCode.OK, "User updated successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Failed to update user")
        }
    }

    // Delete user
    delete("/users/{id}") {
        val id = UUID.fromString(call.parameters["id"])
        val userService = call.scope.get<UserService>()
        try {
            userService.delete(id)
            call.respond(HttpStatusCode.OK, "User deleted successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}