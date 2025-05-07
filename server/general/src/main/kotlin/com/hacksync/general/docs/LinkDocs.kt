package com.hacksync.general.docs

import com.hacksync.general.entities.Link
import com.hacksync.general.models.LinkWithEntity
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.util.*

object LinkDocs {
    val searchLinks: RouteConfig.() -> Unit = {
        operationId = "searchLinks"
        summary = "Search links by title"
        description = "Returns all links whose titles match the search query (case-insensitive) along with their associated entities"
        tags = listOf("Links")

        response {
            HttpStatusCode.OK to {
                description = "List of matching links with their associated entities"
                jsonBody<List<LinkWithEntity>> {
                    example("Search Results") {
                        value = listOf(
                            LinkWithEntity(
                                id = UUID.randomUUID(),
                                url = "/hackathons/123",
                                title = "Hackathon: Summer 2023",
                                entityId = UUID.randomUUID(),
                                entityType = "HACKATHON",
                                entityInfo = null
                            )
                        )
                        description = "Example of search results with a hackathon link"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Title parameter is missing"
            }
        }
    }

    val getAllLinks: RouteConfig.() -> Unit = {
        operationId = "getAllLinks"
        summary = "Get all links"
        description = "Retrieves a list of all links in the system"
        tags = listOf("Links")

        response {
            HttpStatusCode.OK to {
                description = "List of all links"
                jsonBody<List<Link>> {
                    example("All Links") {
                        value = listOf(
                            Link(
                                id = UUID.randomUUID(),
                                url = "/hackathons/123",
                                title = "Hackathon: Summer 2023",
                                entityId = UUID.randomUUID(),
                                entityType = "HACKATHON"
                            )
                        )
                        description = "Example of all links in the system"
                    }
                }
            }
        }
    }

    val getLinkById: RouteConfig.() -> Unit = {
        operationId = "getLinkById"
        summary = "Get link by ID"
        description = "Retrieves a specific link by its ID"
        tags = listOf("Links")

        response {
            HttpStatusCode.OK to {
                description = "Link retrieved successfully"
                jsonBody<Link> {
                    example("Link Details") {
                        value = Link(
                            id = UUID.randomUUID(),
                            url = "/hackathons/123",
                            title = "Hackathon: Summer 2023",
                            entityId = UUID.randomUUID(),
                            entityType = "HACKATHON"
                        )
                        description = "Example of a link with its details"
                    }
                }
            }
            HttpStatusCode.NotFound to {
                description = "Link not found"
            }
        }
    }
} 