package com.hacksync.general.routing

import com.hacksync.general.commands.task.CreateTaskCommand
import com.hacksync.general.commands.task.UpdateTaskCommand
import com.hacksync.general.entities.Task
import com.hacksync.general.services.TaskService
import com.hacksync.general.docs.TaskDocs
import com.hacksync.general.services.KanbanStatusService
import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import java.time.Instant
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

        patch("/{id}", TaskDocs.updateTask) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val command = call.receive<UpdateTaskCommand>()
            val taskService = call.scope.get<TaskService>()
            val statusService = call.scope.get<KanbanStatusService>()
            val task = taskService.getById(id)
                ?: return@patch call.respond(HttpStatusCode.NotFound, "Task not found")
            
            val updatedTask = task.copy(
                status = command.statusId ?: task.status,
                name = command.name ?: task.name,
                description = command.description ?: task.description,
                priority = command.priority ?: task.priority,
                dueDate = command.dueDate ?: task.dueDate,
                updatedAt = Instant.now()
            )
            taskService.update(updatedTask)
            call.respond(HttpStatusCode.OK, updatedTask.toDto())
        }

        delete("/{id}", TaskDocs.deleteTask) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            call.scope.get<TaskService>().delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 