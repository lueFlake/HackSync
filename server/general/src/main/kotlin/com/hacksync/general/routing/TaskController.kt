package com.hacksync.general.routing

import com.hacksync.general.commands.task.CreateTaskCommand
import com.hacksync.general.entities.Task
import com.hacksync.general.services.TaskService
import com.hacksync.general.docs.TaskDocs
import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import java.util.UUID

fun Route.addTaskRoutes() {
    route("/tasks") {
        get("", TaskDocs.getAllTasks) {
            val tasks = call.scope.get<TaskService>().getAll()
            call.respond(HttpStatusCode.OK, tasks.map { it.toDto() })
        }

        get("/{id}", TaskDocs.getTaskById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val task = call.scope.get<TaskService>().getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Task not found")

            call.respond(HttpStatusCode.OK, task.toDto())
        }

        get("/user/{userId}", TaskDocs.getTasksByUserId) {
            val userId = UUID.fromString(call.parameters["userId"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")

            val tasks = call.scope.get<TaskService>().getByUserId(userId)
            call.respond(HttpStatusCode.OK, tasks.map { it.toDto() })
        }

        get("/status/{statusId}", TaskDocs.getTasksByStatusId) {
            val statusId = UUID.fromString(call.parameters["statusId"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid status ID format")

            val tasks = call.scope.get<TaskService>().getByStatusId(statusId)
            call.respond(HttpStatusCode.OK, tasks.map { it.toDto() })
        }

        post("", TaskDocs.createTask) {
            val command = call.receive<CreateTaskCommand>()
            val task = call.scope.get<TaskService>().create(command)
            call.respond(HttpStatusCode.Created, task.toDto())
        }

        put("/{id}", TaskDocs.updateTask) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val task = call.receive<Task>().copy(id = id)
            call.scope.get<TaskService>().update(task)
            call.respond(HttpStatusCode.OK, task.toDto())
        }

        delete("/{id}", TaskDocs.deleteTask) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            call.scope.get<TaskService>().delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 