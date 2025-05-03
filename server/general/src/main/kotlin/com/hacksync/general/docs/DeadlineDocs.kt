package com.hacksync.general.docs

import com.hacksync.general.entities.Deadline
import com.hacksync.general.models.DeadlineCreateRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.util.*
import java.time.LocalDate

object DeadlineDocs {
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
} 