package com.hacksync.general.docs

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.models.KanbanStatusCreateRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.util.*

object KanbanStatusDocs {
    val getAllKanbanStatuses: RouteConfig.() -> Unit = {
        operationId = "getAllKanbanStatuses"
        summary = "Get all Kanban statuses"
        description = "Retrieves a list of all Kanban statuses"
        tags = listOf("Kanban")

        response {
            HttpStatusCode.OK to {
                description = "List of Kanban statuses retrieved successfully"
            }
        }
    }

    val getKanbanStatusById: RouteConfig.() -> Unit = {
        operationId = "getKanbanStatusById"
        summary = "Get Kanban status by ID"
        description = "Retrieves a specific Kanban status by its ID"
        tags = listOf("Kanban")

        response {
            HttpStatusCode.OK to {
                description = "Kanban status retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
        }
    }

    val createKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "createKanbanStatus"
        summary = "Create new Kanban status"
        description = "Creates a new Kanban status"
        tags = listOf("Kanban")

        request {
            jsonBody<KanbanStatusCreateRequest> {
                example("Create Kanban Status") {
                    value = KanbanStatusCreateRequest(
                        name = "In Progress",
                        color = "#FFA500",
                        nextId = null
                    )
                    description = "Create a new Kanban status with name, color and optional next status ID"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Kanban status created successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
        }
    }

    val updateKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "updateKanbanStatus"
        summary = "Update Kanban status"
        description = "Updates an existing Kanban status"
        tags = listOf("Kanban")

        request {
            jsonBody<KanbanStatusCreateRequest> {
                example("Update Kanban Status") {
                    value = KanbanStatusCreateRequest(
                        name = "Updated Status",
                        color = "#00FF00",
                        nextId = null
                    )
                    description = "Update an existing Kanban status"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Kanban status updated successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
        }
    }

    val deleteKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "deleteKanbanStatus"
        summary = "Delete Kanban status"
        description = "Deletes a Kanban status by its ID"
        tags = listOf("Kanban")

        response {
            HttpStatusCode.NoContent to {
                description = "Kanban status deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
        }
    }
} 