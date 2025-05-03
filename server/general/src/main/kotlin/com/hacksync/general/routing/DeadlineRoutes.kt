package com.hacksync.general.routing

import com.hacksync.general.entities.Deadline
import com.hacksync.general.entities.Link
import com.hacksync.general.models.DeadlineCreateRequest
import com.hacksync.general.models.DeadlineResponse
import com.hacksync.general.services.DeadlineService
import com.hacksync.general.docs.DeadlineDocs
import com.hacksync.general.utils.jsonBody
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.scope
import java.util.UUID
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.LocalDate

val getAllDeadlines: RouteConfig.() -> Unit = {
    operationId = "getAllDeadlines"
    summary = "Get all deadlines"
    description = "Retrieves a list of all deadlines"
    tags = listOf("Deadlines")

    response {
        HttpStatusCode.OK to {
            description = "List of deadlines retrieved successfully"
        }
    }
}

val getDeadlineById: RouteConfig.() -> Unit = {
    operationId = "getDeadlineById"
    summary = "Get deadline by ID"
    description = "Retrieves a specific deadline by its ID"
    tags = listOf("Deadlines")

    response {
        HttpStatusCode.OK to {
            description = "Deadline retrieved successfully"
        }
        HttpStatusCode.NotFound to {
            description = "Deadline not found"
        }
    }
}

val createDeadline: RouteConfig.() -> Unit = {
    operationId = "createDeadline"
    summary = "Create new deadline"
    description = "Creates a new deadline"
    tags = listOf("Deadlines")

    request {
        jsonBody<DeadlineCreateRequest> {
            example("Create Deadline") {
                value = DeadlineCreateRequest(
                    deadline = Deadline(
                        id = UUID.randomUUID(),
                        date = LocalDate.now().plusDays(7),
                        linkId = null,
                        name = "Project Deadline",
                        type = "PROJECT"
                    )
                )
                description = "Create a new deadline with name, date and type"
            }
        }
    }

    response {
        HttpStatusCode.Created to {
            description = "Deadline created successfully"
        }
        HttpStatusCode.BadRequest to {
            description = "Invalid input data"
        }
    }
}

val updateDeadline: RouteConfig.() -> Unit = {
    operationId = "updateDeadline"
    summary = "Update deadline"
    description = "Updates an existing deadline"
    tags = listOf("Deadlines")

    request {
        jsonBody<DeadlineCreateRequest> {
            example("Update Deadline") {
                value = DeadlineCreateRequest(
                    deadline = Deadline(
                        id = UUID.randomUUID(),
                        date = LocalDate.now().plusDays(14),
                        linkId = null,
                        name = "Updated Project Deadline",
                        type = "PROJECT"
                    )
                )
                description = "Update an existing deadline"
            }
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Deadline updated successfully"
        }
        HttpStatusCode.BadRequest to {
            description = "Invalid input data"
        }
        HttpStatusCode.NotFound to {
            description = "Deadline not found"
        }
    }
}

val deleteDeadline: RouteConfig.() -> Unit = {
    operationId = "deleteDeadline"
    summary = "Delete deadline"
    description = "Deletes a deadline by its ID"
    tags = listOf("Deadlines")

    response {
        HttpStatusCode.NoContent to {
            description = "Deadline deleted successfully"
        }
        HttpStatusCode.NotFound to {
            description = "Deadline not found"
        }
    }
}

fun Route.deadlineRoutes() {
    route("/deadlines") {
        val deadlineService by inject<DeadlineService>()

        get("", DeadlineDocs.getAllDeadlines) {
            val deadlines = deadlineService.getAll()
            call.respond(HttpStatusCode.OK, deadlines)
        }

        get("/{id}", DeadlineDocs.getDeadlineById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val deadline = deadlineService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Deadline not found")

            call.respond(HttpStatusCode.OK, deadline)
        }

        post("", DeadlineDocs.createDeadline) {
            val request = call.receive<DeadlineCreateRequest>()
            val createdDeadline = deadlineService.create(request.deadline)
            call.respond(HttpStatusCode.Created, createdDeadline)
        }

        put("/{id}", DeadlineDocs.updateDeadline) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val request = call.receive<DeadlineCreateRequest>()
            val updatedDeadline = deadlineService.update(request.deadline)
            call.respond(HttpStatusCode.OK, updatedDeadline)
        }

        delete("/{id}", DeadlineDocs.deleteDeadline) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            deadlineService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 