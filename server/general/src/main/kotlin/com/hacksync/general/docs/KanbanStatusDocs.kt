package com.hacksync.general.docs

import com.hacksync.general.commands.status.CreateKanbanStatusCommand
import com.hacksync.general.commands.status.UpdateKanbanStatusCommand
import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.utils.jsonBody
import com.hacksync.general.utils.standardListQueryParameters
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.Instant
import java.util.*

object KanbanStatusDocs {
    val getAllKanbanStatuses: RouteConfig.() -> Unit = {
        operationId = "getAllKanbanStatuses"
        summary = "Get all Kanban statuses"
        description = "Retrieves a list of all Kanban statuses in the system. Kanban statuses represent different stages in the task workflow."
        tags = listOf("Kanban")

        request {
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "List of Kanban statuses retrieved successfully"
                jsonBody<List<KanbanStatus>> {
                    example("All Kanban Statuses") {
                        value = listOf(
                            KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = UUID.randomUUID(),
                                name = "To Do",
                                color = "#FF0000",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = UUID.randomUUID(),
                                name = "In Progress",
                                color = "#FFA500",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = null,
                                name = "Done",
                                color = "#00FF00",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of a typical Kanban board setup with three statuses"
                    }
                }
            }
        }
    }

    val getKanbanStatusById: RouteConfig.() -> Unit = {
        operationId = "getKanbanStatusById"
        summary = "Get Kanban status by ID"
        description = "Retrieves a specific Kanban status by its UUID. The status includes its name, color, and reference to the next status in the workflow."
        tags = listOf("Kanban")

        request {
            pathParameter<UUID>("id") {
                description = "The UUID of the Kanban status to retrieve"
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Kanban status retrieved successfully"
                jsonBody<KanbanStatus> {
                    example("Single Kanban Status") {
                        value = KanbanStatus(
                            id = UUID.randomUUID(),
                            nextId = UUID.randomUUID(),
                            name = "In Progress",
                            color = "#FFA500",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a Kanban status in the middle of a workflow"
                    }
                }
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid UUID format"
            }
        }
    }

    val createKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "createKanbanStatus"
        summary = "Create new Kanban status"
        description = "Creates a new Kanban status in the system. The status can be linked to another status to create a workflow sequence."
        tags = listOf("Kanban")

        request {
            jsonBody<CreateKanbanStatusCommand> {
                example("Create Kanban Status") {
                    value = CreateKanbanStatusCommand(
                        name = "In Progress",
                        color = "#FFA500",
                        nextId = UUID.randomUUID()
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
                            nextId = UUID.randomUUID(),
                            name = "In Progress",
                            color = "#FFA500",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a newly created Kanban status with a link to the next status"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or nextId references a non-existent status"
            }
        }
    }

    val updateKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "updateKanbanStatus"
        summary = "Update Kanban status"
        description = "Updates an existing Kanban status. All fields are optional and only the provided fields will be updated."
        tags = listOf("Kanban")

        request {
            pathParameter<UUID>("id") {
                description = "The UUID of the Kanban status to update"
            }
            jsonBody<UpdateKanbanStatusCommand> {
                example("Update Kanban Status") {
                    value = UpdateKanbanStatusCommand(
                        id = UUID.randomUUID(),
                        name = "Updated Status",
                        color = "#00FF00",
                        nextId = UUID.randomUUID()
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
                            nextId = UUID.randomUUID(),
                            name = "Updated Status",
                            color = "#00FF00",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of an updated Kanban status with new name, color, and next status"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data, ID mismatch, or nextId references a non-existent status"
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
        }
    }

    val deleteKanbanStatus: RouteConfig.() -> Unit = {
        operationId = "deleteKanbanStatus"
        summary = "Delete Kanban status"
        description = "Deletes a Kanban status by its UUID. Note that this operation will fail if there are tasks currently using this status."
        tags = listOf("Kanban")

        request {
            pathParameter<UUID>("id") {
                description = "The UUID of the Kanban status to delete"
            }
        }

        response {
            HttpStatusCode.NoContent to {
                description = "Kanban status deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Kanban status not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid UUID format or status is in use by tasks"
            }
        }
    }
} 