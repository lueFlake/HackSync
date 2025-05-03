package com.hacksync.general.routing

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.models.KanbanStatusCreateRequest
import com.hacksync.general.services.KanbanStatusService
import com.hacksync.general.docs.KanbanStatusDocs
import com.hacksync.general.utils.jsonBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.UUID
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.delete

fun Route.kanbanStatusRoutes() {
    route("/kanban-statuses") {
        val kanbanStatusService by inject<KanbanStatusService>()

        get("", KanbanStatusDocs.getAllKanbanStatuses) {
            val statuses = kanbanStatusService.getAll()
            call.respond(HttpStatusCode.OK, statuses)
        }

        get("/{id}", KanbanStatusDocs.getKanbanStatusById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val status = kanbanStatusService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Kanban status not found")

            call.respond(HttpStatusCode.OK, status)
        }

        post("", KanbanStatusDocs.createKanbanStatus) {
            val request = call.receive<KanbanStatusCreateRequest>()
            val status = KanbanStatus(
                id = UUID.randomUUID(),
                name = request.name,
                color = request.color,
                nextId = request.nextId
            )
            val createdStatus = kanbanStatusService.create(status)
            call.respond(HttpStatusCode.Created, createdStatus)
        }

        put("/{id}", KanbanStatusDocs.updateKanbanStatus) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val request = call.receive<KanbanStatusCreateRequest>()
            val status = KanbanStatus(
                id = id,
                name = request.name,
                color = request.color,
                nextId = request.nextId
            )
            val updatedStatus = kanbanStatusService.update(status)
            call.respond(HttpStatusCode.OK, updatedStatus)
        }

        delete("/{id}", KanbanStatusDocs.deleteKanbanStatus) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            kanbanStatusService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 