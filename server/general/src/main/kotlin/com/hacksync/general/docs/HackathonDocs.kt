package com.hacksync.general.docs

import com.hacksync.general.commands.hackathon.CreateHackathonCommand
import com.hacksync.general.commands.hackathon.UpdateHackathonCommand
import com.hacksync.general.dto.HackathonDto
import com.hacksync.general.entities.Hackathon
import com.hacksync.general.utils.jsonBody
import com.hacksync.general.utils.standardListQueryParameters
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.Instant
import java.util.*

object HackathonDocs {
    val getAllHackathons: RouteConfig.() -> Unit = {
        operationId = "getAllHackathons"
        summary = "Get all hackathons"
        description = "Retrieves a list of all hackathons"
        tags = listOf("Hackathons")

        request {
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "List of hackathons retrieved successfully"
                jsonBody<List<HackathonDto>> {
                    example("All Hackathons") {
                        value = listOf(
                            HackathonDto(
                                id = UUID.randomUUID(),
                                description = "Annual coding competition",
                                dateOfRegister = Instant.now(),
                                dateOfStart = Instant.now().plusSeconds(86400),
                                dateOfEnd = Instant.now().plusSeconds(259200),
                                name = "Hackathon 2024",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of all hackathons in the system"
                    }
                }
            }
        }
    }

    val getHackathonById: RouteConfig.() -> Unit = {
        operationId = "getHackathonById"
        summary = "Get hackathon by ID"
        description = "Retrieves a specific hackathon by its ID"
        tags = listOf("Hackathons")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the hackathon to retrieve"
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Hackathon retrieved successfully"
                jsonBody<Hackathon> {
                    example("Single Hackathon") {
                        value = Hackathon(
                            id = UUID.randomUUID(),
                            description = "Annual coding competition",
                            dateOfRegister = Instant.now(),
                            dateOfStart = Instant.now().plusSeconds(86400),
                            dateOfEnd = Instant.now().plusSeconds(259200),
                            name = "Hackathon 2024",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a single hackathon"
                    }
                }
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }

    val createHackathon: RouteConfig.() -> Unit = {
        operationId = "createHackathon"
        summary = "Create new hackathon"
        description = "Creates a new hackathon"
        tags = listOf("Hackathons")

        request {
            jsonBody<CreateHackathonCommand> {
                example("Create Hackathon") {
                    value = CreateHackathonCommand(
                        name = "Hackathon 2024",
                        description = "Annual coding competition",
                        dateOfRegister = Instant.now().toString(),
                        dateOfStart = Instant.now().plusSeconds(86400).toString(),
                        dateOfEnd = Instant.now().plusSeconds(259200).toString()
                    )
                    description = "Create a new hackathon with all required fields"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Hackathon created successfully"
                jsonBody<Hackathon> {
                    example("Created Hackathon") {
                        value = Hackathon(
                            id = UUID.randomUUID(),
                            description = "Annual coding competition",
                            dateOfRegister = Instant.now(),
                            dateOfStart = Instant.now().plusSeconds(86400),
                            dateOfEnd = Instant.now().plusSeconds(259200),
                            name = "Hackathon 2024",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a created hackathon"
                    }
                }
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
        tags = listOf("Hackathons")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the hackathon to update"
            }
            jsonBody<UpdateHackathonCommand> {
                example("Update Hackathon") {
                    value = UpdateHackathonCommand(
                        id = UUID.randomUUID(),
                        name = "Updated Hackathon Name",
                        description = "Updated description",
                        dateOfRegister = Instant.now().toString(),
                        dateOfStart = Instant.now().plusSeconds(86400).toString(),
                        dateOfEnd = Instant.now().plusSeconds(259200).toString()
                    )
                    description = "Update an existing hackathon with optional fields"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Hackathon updated successfully"
                jsonBody<HackathonDto> {
                    example("Updated Hackathon") {
                        value = HackathonDto(
                            id = UUID.randomUUID(),
                            description = "Updated description",
                            dateOfRegister = Instant.now(),
                            dateOfStart = Instant.now().plusSeconds(86400),
                            dateOfEnd = Instant.now().plusSeconds(259200),
                            name = "Updated Hackathon Name",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of an updated hackathon"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or ID mismatch"
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
        tags = listOf("Hackathons")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the hackathon to delete"
            }
        }

        response {
            HttpStatusCode.NoContent to {
                description = "Hackathon deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Hackathon not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }
} 