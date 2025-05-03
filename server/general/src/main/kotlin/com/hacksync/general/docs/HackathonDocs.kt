package com.hacksync.general.docs

import com.hacksync.general.entities.Hackathon
import com.hacksync.general.models.HackathonCreateRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.LocalDate
import java.util.*

object HackathonDocs {
    val getAllHackathons: RouteConfig.() -> Unit = {
        operationId = "getAllHackathons"
        summary = "Get all hackathons"
        description = "Retrieves a list of all hackathons"
        tags = listOf("Hackathon")

        response {
            HttpStatusCode.OK to {
                description = "List of hackathons retrieved successfully"
            }
        }
    }

    val getHackathonById: RouteConfig.() -> Unit = {
        operationId = "getHackathonById"
        summary = "Get hackathon by ID"
        description = "Retrieves a specific hackathon by its ID"
        tags = listOf("Hackathon")

        response {
            HttpStatusCode.OK to {
                description = "Hackathon retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
        }
    }

    val createHackathon: RouteConfig.() -> Unit = {
        operationId = "createHackathon"
        summary = "Create new hackathon"
        description = "Creates a new hackathon"
        tags = listOf("Hackathon")

        request {
            jsonBody<HackathonCreateRequest> {
                example("Create Hackathon") {
                    value = HackathonCreateRequest(
                        hackathon = Hackathon(
                            id = UUID.randomUUID(),
                            description = "Main hackathon event",
                            dateOfRegister = LocalDate.now(),
                            dateOfStart = LocalDate.now(),
                            dateOfEnd = LocalDate.now().plusMonths(1),
                            extraDestfine = "Additional hackathon details",
                            linkId = null,
                            name = "Main Hackathon"
                        )
                    )
                    description = "Create a new hackathon with name, description and dates"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Hackathon created successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
        }
    }

    val updateHackathon: RouteConfig.() -> Unit = {
        operationId = "updateHackathon"
        summary = "Update hackathon"
        description = "Updates an existing hackathon"
        tags = listOf("Hackathon")

        request {
            jsonBody<HackathonCreateRequest> {
                example("Update Hackathon") {
                    value = HackathonCreateRequest(
                        hackathon = Hackathon(
                            id = UUID.randomUUID(),
                            description = "Updated hackathon event",
                            dateOfRegister = LocalDate.now(),
                            dateOfStart = LocalDate.now(),
                            dateOfEnd = LocalDate.now().plusMonths(2),
                            extraDestfine = "Updated hackathon details",
                            linkId = null,
                            name = "Updated Hackathon"
                        )
                    )
                    description = "Update an existing hackathon"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Hackathon updated successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
        }
    }

    val deleteHackathon: RouteConfig.() -> Unit = {
        operationId = "deleteHackathon"
        summary = "Delete hackathon"
        description = "Deletes a hackathon by its ID"
        tags = listOf("Hackathon")

        response {
            HttpStatusCode.NoContent to {
                description = "Hackathon deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
        }
    }
} 