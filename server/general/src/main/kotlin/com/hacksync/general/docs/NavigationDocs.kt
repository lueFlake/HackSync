package com.hacksync.general.docs

import com.hacksync.general.entities.Navigation
import com.hacksync.general.models.NavigationCreateRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.LocalDate
import java.util.*

object NavigationDocs {
    val getAllNavigations: RouteConfig.() -> Unit = {
        operationId = "getAllNavigations"
        summary = "Get all navigations"
        description = "Retrieves a list of all navigations"
        tags = listOf("Navigation")

        response {
            HttpStatusCode.OK to {
                description = "List of navigations retrieved successfully"
            }
        }
    }

    val getNavigationById: RouteConfig.() -> Unit = {
        operationId = "getNavigationById"
        summary = "Get navigation by ID"
        description = "Retrieves a specific navigation by its ID"
        tags = listOf("Navigation")

        response {
            HttpStatusCode.OK to {
                description = "Navigation retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Navigation not found"
            }
        }
    }

    val createNavigation: RouteConfig.() -> Unit = {
        operationId = "createNavigation"
        summary = "Create new navigation"
        description = "Creates a new navigation"
        tags = listOf("Navigation")

        request {
            jsonBody<NavigationCreateRequest> {
                example("Create Navigation") {
                    value = NavigationCreateRequest(
                        navigation = Navigation(
                            id = UUID.randomUUID(),
                            description = "Main navigation menu",
                            dateOfRegister = LocalDate.now(),
                            dateOfStart = LocalDate.now(),
                            dateOfEnd = LocalDate.now().plusMonths(1),
                            extraDestfine = "Additional navigation details",
                            linkId = null,
                            name = "Main Navigation"
                        )
                    )
                    description = "Create a new navigation with name, description and dates"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Navigation created successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
        }
    }

    val updateNavigation: RouteConfig.() -> Unit = {
        operationId = "updateNavigation"
        summary = "Update navigation"
        description = "Updates an existing navigation"
        tags = listOf("Navigation")

        request {
            jsonBody<NavigationCreateRequest> {
                example("Update Navigation") {
                    value = NavigationCreateRequest(
                        navigation = Navigation(
                            id = UUID.randomUUID(),
                            description = "Updated navigation menu",
                            dateOfRegister = LocalDate.now(),
                            dateOfStart = LocalDate.now(),
                            dateOfEnd = LocalDate.now().plusMonths(2),
                            extraDestfine = "Updated navigation details",
                            linkId = null,
                            name = "Updated Navigation"
                        )
                    )
                    description = "Update an existing navigation"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Navigation updated successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "Navigation not found"
            }
        }
    }

    val deleteNavigation: RouteConfig.() -> Unit = {
        operationId = "deleteNavigation"
        summary = "Delete navigation"
        description = "Deletes a navigation by its ID"
        tags = listOf("Navigation")

        response {
            HttpStatusCode.NoContent to {
                description = "Navigation deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Navigation not found"
            }
        }
    }
} 