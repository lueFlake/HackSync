package com.hacksync.general.docs

import com.hacksync.general.entities.Team
import com.hacksync.general.models.TeamCreateRequest
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.util.*

object TeamDocs {
    val getAllTeams: RouteConfig.() -> Unit = {
        operationId = "getAllTeams"
        summary = "Get all teams"
        description = "Retrieves a list of all teams"
        tags = listOf("Teams")

        response {
            HttpStatusCode.OK to {
                description = "List of teams retrieved successfully"
            }
        }
    }

    val getTeamById: RouteConfig.() -> Unit = {
        operationId = "getTeamById"
        summary = "Get team by ID"
        description = "Retrieves a specific team by its ID"
        tags = listOf("Teams")

        response {
            HttpStatusCode.OK to {
                description = "Team retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
        }
    }

    val createTeam: RouteConfig.() -> Unit = {
        operationId = "createTeam"
        summary = "Create new team"
        description = "Creates a new team"
        tags = listOf("Teams")

        request {
            jsonBody<TeamCreateRequest> {
                example("Create Team") {
                    value = TeamCreateRequest(
                        team = Team(
                            id = UUID.randomUUID(),
                            linkId = null,
                            name = "Development Team"
                        )
                    )
                    description = "Create a new team with name"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Team created successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
        }
    }

    val updateTeam: RouteConfig.() -> Unit = {
        operationId = "updateTeam"
        summary = "Update team"
        description = "Updates an existing team"
        tags = listOf("Teams")

        request {
            jsonBody<TeamCreateRequest> {
                example("Update Team") {
                    value = TeamCreateRequest(
                        team = Team(
                            id = UUID.randomUUID(),
                            linkId = null,
                            name = "Updated Team Name"
                        )
                    )
                    description = "Update an existing team"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Team updated successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
        }
    }

    val deleteTeam: RouteConfig.() -> Unit = {
        operationId = "deleteTeam"
        summary = "Delete team"
        description = "Deletes a team by its ID"
        tags = listOf("Teams")

        response {
            HttpStatusCode.NoContent to {
                description = "Team deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
        }
    }

    val addUserToTeam: RouteConfig.() -> Unit = {
        operationId = "addUserToTeam"
        summary = "Add user to team"
        description = "Adds a user to a team"
        tags = listOf("Teams")

        response {
            HttpStatusCode.OK to {
                description = "User added to team successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "Team or user not found"
            }
        }
    }

    val removeUserFromTeam: RouteConfig.() -> Unit = {
        operationId = "removeUserFromTeam"
        summary = "Remove user from team"
        description = "Removes a user from a team"
        tags = listOf("Teams")

        response {
            HttpStatusCode.OK to {
                description = "User removed from team successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "Team or user not found"
            }
        }
    }

    val getTeamMembers: RouteConfig.() -> Unit = {
        operationId = "getTeamMembers"
        summary = "Get team members"
        description = "Retrieves all members of a team"
        tags = listOf("Teams")

        response {
            HttpStatusCode.OK to {
                description = "Team members retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
        }
    }

    val getUserTeams: RouteConfig.() -> Unit = {
        operationId = "getUserTeams"
        summary = "Get user teams"
        description = "Retrieves all teams a user belongs to"
        tags = listOf("Teams")

        response {
            HttpStatusCode.OK to {
                description = "User teams retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "User not found"
            }
        }
    }
} 