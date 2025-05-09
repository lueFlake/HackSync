package com.hacksync.general.routing

import com.hacksync.general.commands.status.CreateKanbanStatusCommand
import com.hacksync.general.commands.status.DeleteKanbanStatusCommand
import com.hacksync.general.commands.status.ReadKanbanStatusCommand
import com.hacksync.general.commands.status.UpdateKanbanStatusCommand
import com.hacksync.general.services.KanbanStatusService
import com.hacksync.general.docs.KanbanStatusDocs
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.delete
import org.koin.ktor.plugin.scope

fun Route.addkanbanStatusRoutes() {
    route("/kanban-statuses") {
        // Get all kanban statuses
        get("", KanbanStatusDocs.getAllKanbanStatuses) {
            val statuses = call.scope.get<KanbanStatusService>().getAll()
            call.respond(statuses)
        }

        // Get kanban status by ID
        get("/{id}", KanbanStatusDocs.getKanbanStatusById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val command = ReadKanbanStatusCommand(id)
            val status = call.scope.get<KanbanStatusService>().getById(command.id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Kanban status not found")

            call.respond(status)
        }

        // Create new kanban status
        post("", KanbanStatusDocs.createKanbanStatus) {
            val command = call.receive<CreateKanbanStatusCommand>()
            val status = command.execute()
            call.scope.get<KanbanStatusService>().create(status)
            call.respond(HttpStatusCode.Created, status)
        }

        // Update kanban status
        put("/{id}", KanbanStatusDocs.updateKanbanStatus) {
            val kanbanStatusService = call.scope.get<KanbanStatusService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val command = call.receive<UpdateKanbanStatusCommand>()
            if (command.id != id) {
                return@put call.respond(HttpStatusCode.BadRequest, "ID in path does not match ID in body")
            }

            val existingStatus = kanbanStatusService.getById(id)
                ?: return@put call.respond(HttpStatusCode.NotFound, "Kanban status not found")

            val updatedStatus = existingStatus.copy(
                name = command.name ?: existingStatus.name,
                color = command.color ?: existingStatus.color,
                nextId = command.nextId ?: existingStatus.nextId,
                updatedAt = java.time.Instant.now()
            )

            kanbanStatusService.update(updatedStatus)
            call.respond(updatedStatus)
        }

        // Delete kanban status
        delete("/{id}", KanbanStatusDocs.deleteKanbanStatus) {
            val kanbanStatusService = call.scope.get<KanbanStatusService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val command = DeleteKanbanStatusCommand(id)
            val existingStatus = kanbanStatusService.getById(command.id)
                ?: return@delete call.respond(HttpStatusCode.NotFound, "Kanban status not found")

            kanbanStatusService.delete(command.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 