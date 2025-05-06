package com.hacksync.general.docs

import com.hacksync.general.commands.status.CreateKanbanStatusCommand
import com.hacksync.general.commands.status.UpdateKanbanStatusCommand
import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.Instant
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
                jsonBody<List<KanbanStatus>> {
                    example("All Kanban Statuses") {
                        value = listOf(
                            KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = null,
                                name = "To Do",
                                color = "#FF0000",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = null,
                                name = "In Progress",
                                color = "#FFA500",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of all Kanban statuses in the system"
                    }
                }
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
                jsonBody<KanbanStatus> {
                    example("Single Kanban Status") {
                        value = KanbanStatus(
                            id = UUID.randomUUID(),
                            nextId = null,
                            name = "In Progress",
                            color = "#FFA500",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a single Kanban status"
                    }
                }
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }

    val createKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "createKanbanStatus"
        summary = "Create new Kanban status"
        description = "Creates a new Kanban status"
        tags = listOf("Kanban")

        request {
            jsonBody<CreateKanbanStatusCommand> {
                example("Create Kanban Status") {
                    value = CreateKanbanStatusCommand(
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
                jsonBody<KanbanStatus> {
                    example("Created Kanban Status") {
                        value = KanbanStatus(
                            id = UUID.randomUUID(),
                            nextId = null,
                            name = "In Progress",
                            color = "#FFA500",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a created Kanban status"
                    }
                }
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
            jsonBody<UpdateKanbanStatusCommand> {
                example("Update Kanban Status") {
                    value = UpdateKanbanStatusCommand(
                        id = UUID.randomUUID(),
                        name = "Updated Status",
                        color = "#00FF00",
                        nextId = null
                    )
                    description = "Update an existing Kanban status with optional fields"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Kanban status updated successfully"
                jsonBody<KanbanStatus> {
                    example("Updated Kanban Status") {
                        value = KanbanStatus(
                            id = UUID.randomUUID(),
                            nextId = null,
                            name = "Updated Status",
                            color = "#00FF00",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of an updated Kanban status"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or ID mismatch"
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
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }
} 