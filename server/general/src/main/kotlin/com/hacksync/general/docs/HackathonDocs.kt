package com.hacksync.general.docs

import com.hacksync.general.commands.*
import com.hacksync.general.entities.Hackathon
import com.hacksync.general.models.HackathonCreateRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import java.util.*

object HackathonDocs {
    val postHackathon: RouteConfig.() -> Unit = {
        operationId = "postHackathon"
        summary = "Create a new hackathon"
        description = "Creates a new hackathon and returns its ID. Dates should be in ISO-8601 format (e.g., 2024-03-20T10:00:00Z)"
        tags = listOf("Hackathons")

        request {
            jsonBody<CreateHackathonCommand> {
                example("Create Hackathon") {
                    value = CreateHackathonCommand(
                        name = "Hackathon 2024",
                        description = "Annual coding competition",
                        dateOfRegister = "2024-03-20T10:00:00Z",
                        dateOfStart = "2024-04-01T09:00:00Z",
                        dateOfEnd = "2024-04-03T18:00:00Z",
                        extraDestfine = "Additional information about the hackathon"
                    )
                    description = "Create a new hackathon with name, description, and dates in ISO-8601 format"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Hackathon created successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or date format"
            }
        }
    }

    val getHackathonById: RouteConfig.() -> Unit = {
        operationId = "getHackathonById"
        summary = "Get hackathon by ID"
        description = "Retrieves a hackathon by its UUID"
        tags = listOf("Hackathons")

        response {
            HttpStatusCode.OK to {
                description = "Hackathon found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
        }
    }

    val getAllHackathons: RouteConfig.() -> Unit = {
        operationId = "getAllHackathons"
        summary = "Get all hackathons"
        description = "Retrieves a list of all hackathons"
        tags = listOf("Hackathons")

        response {
            HttpStatusCode.OK to {
                description = "List of hackathons"
            }
        }
    }

    val putHackathon: RouteConfig.() -> Unit = {
        operationId = "putHackathon"
        summary = "Update hackathon"
        description = "Updates an existing hackathon's information. Dates should be in ISO-8601 format (e.g., 2024-03-20T10:00:00Z)"
        tags = listOf("Hackathons")

        request {
            jsonBody<UpdateHackathonCommand> {
                example("Update Hackathon") {
                    value = UpdateHackathonCommand(
                        id = UUID.randomUUID(),
                        name = "Updated Hackathon Name",
                        description = "Updated description",
                        dateOfRegister = "2024-03-25T10:00:00Z",
                        dateOfStart = "2024-04-05T09:00:00Z",
                        dateOfEnd = "2024-04-07T18:00:00Z",
                        extraDestfine = "Updated additional information"
                    )
                    description = "Update hackathon's information with dates in ISO-8601 format"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Hackathon updated successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or date format"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
        }
    }

    val deleteHackathon: RouteConfig.() -> Unit = {
        operationId = "deleteHackathon"
        summary = "Delete hackathon"
        description = "Deletes a hackathon by its UUID"
        tags = listOf("Hackathons")

        response {
            HttpStatusCode.OK to {
                description = "Hackathon deleted successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
        }
    }
} 