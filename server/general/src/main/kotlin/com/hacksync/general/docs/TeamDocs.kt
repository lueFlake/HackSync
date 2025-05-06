package com.hacksync.general.docs

import com.hacksync.general.commands.*
import com.hacksync.general.commands.team.AddUserToTeamCommand
import com.hacksync.general.commands.team.CreateTeamCommand
import com.hacksync.general.commands.team.RemoveUserFromTeamCommand
import com.hacksync.general.commands.team.UpdateTeamCommand
import com.hacksync.general.entities.Team
import com.hacksync.general.utils.jsonBody
import com.hacksync.general.utils.standardListQueryParameters
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.Instant
import java.util.*

object TeamDocs {
    val getAllTeams: RouteConfig.() -> Unit = {
        operationId = "getAllTeams"
        summary = "Get all teams"
        description = "Retrieves a list of all teams"
        tags = listOf("Teams")

        request {
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "List of teams retrieved successfully"
                jsonBody<List<Team>> {
                    example("All Teams") {
                        value = listOf(
                            Team(
                                id = UUID.randomUUID(),
                                linkId = UUID.randomUUID(),
                                name = "Development Team",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            Team(
                                id = UUID.randomUUID(),
                                linkId = UUID.randomUUID(),
                                name = "Design Team",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of all teams in the system"
                    }
                }
            }
        }
    }

    val getTeamById: RouteConfig.() -> Unit = {
        operationId = "getTeamById"
        summary = "Get team by ID"
        description = "Retrieves a specific team by its ID"
        tags = listOf("Teams")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the team to retrieve"
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Team retrieved successfully"
                jsonBody<Team> {
                    example("Single Team") {
                        value = Team(
                            id = UUID.randomUUID(),
                            linkId = UUID.randomUUID(),
                            name = "Development Team",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a single team"
                    }
                }
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }

    val createTeam: RouteConfig.() -> Unit = {
        operationId = "createTeam"
        summary = "Create new team"
        description = "Creates a new team"
        tags = listOf("Teams")

        request {
            jsonBody<CreateTeamCommand> {
                example("Create Team") {
                    value = CreateTeamCommand(
                        name = "Development Team"
                    )
                    description = "Create a new team with name"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Team created successfully"
                jsonBody<Team> {
                    example("Created Team") {
                        value = Team(
                            id = UUID.randomUUID(),
                            linkId = UUID.randomUUID(),
                            name = "Development Team",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a created team"
                    }
                }
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
            pathParameter<UUID>("id") {
                description = "The ID of the team to update"
            }
            jsonBody<UpdateTeamCommand> {
                example("Update Team") {
                    value = UpdateTeamCommand(
                        id = UUID.randomUUID(),
                        name = "Updated Team Name"
                    )
                    description = "Update an existing team with optional fields"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Team updated successfully"
                jsonBody<Team> {
                    example("Updated Team") {
                        value = Team(
                            id = UUID.randomUUID(),
                            linkId = UUID.randomUUID(),
                            name = "Updated Team Name",
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of an updated team"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or ID mismatch"
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

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the team to delete"
            }
        }

        response {
            HttpStatusCode.NoContent to {
                description = "Team deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }

    val addUserToTeam: RouteConfig.() -> Unit = {
        operationId = "addUserToTeam"
        summary = "Add user to team"
        description = "Adds a user to a team"
        tags = listOf("Teams")

        request {
            pathParameter<UUID>("teamId") {
                description = "The ID of the team to add the user to"
            }
            jsonBody<AddUserToTeamCommand> {
                example("Add User to Team") {
                    value = AddUserToTeamCommand(
                        teamId = UUID.randomUUID(),
                        userId = UUID.randomUUID()
                    )
                    description = "Add a user to a team by their IDs"
                }
            }
        }

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

        request {
            pathParameter<UUID>("teamId") {
                description = "The ID of the team to remove the user from"
            }
            jsonBody<RemoveUserFromTeamCommand> {
                example("Remove User from Team") {
                    value = RemoveUserFromTeamCommand(
                        teamId = UUID.randomUUID(),
                        userId = UUID.randomUUID()
                    )
                    description = "Remove a user from a team by their IDs"
                }
            }
        }

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

        request {
            pathParameter<UUID>("teamId") {
                description = "The ID of the team to get members from"
            }
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "Team members retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Team not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }

    val getUserTeams: RouteConfig.() -> Unit = {
        operationId = "getUserTeams"
        summary = "Get user teams"
        description = "Retrieves all teams a user belongs to"
        tags = listOf("Teams")

        request {
            pathParameter<UUID>("userId") {
                description = "The ID of the user to get teams from"
            }
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "User teams retrieved successfully"
            }
            HttpStatusCode.NotFound to {
                description = "User not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }
} 