package com.hacksync.general.routing

import com.hacksync.general.commands.team.*
import com.hacksync.general.services.TeamService
import com.hacksync.general.docs.TeamDocs
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import java.util.UUID
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.delete

fun Route.teamRoutes() {
    route("/teams") {

        get("", TeamDocs.getAllTeams) {
            val teamService = call.scope.get<TeamService>()
            val teams = teamService.getAll()
            call.respond(HttpStatusCode.OK, teams.map { it.toDto() })
        }

        get("/{id}", TeamDocs.getTeamById) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val team = teamService.read(ReadTeamCommand(id))
                ?: return@get call.respond(HttpStatusCode.NotFound, "Team not found")

            call.respond(HttpStatusCode.OK, team.toDto())
        }

        post("", TeamDocs.createTeam) {
            val teamService = call.scope.get<TeamService>()
            val command = call.receive<CreateTeamCommand>()
            val createdTeam = teamService.create(command)
            call.respond(HttpStatusCode.Created, createdTeam.toDto())
        }

        put("/{id}", TeamDocs.updateTeam) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val command = call.receive<UpdateTeamCommand>()
            val updatedTeam = teamService.update(command)
            call.respond(HttpStatusCode.OK, updatedTeam.toDto())
        }

        delete("/{id}", TeamDocs.deleteTeam) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            teamService.delete(DeleteTeamCommand(id))
            call.respond(HttpStatusCode.NoContent)
        }

        post("/{teamId}/users/{userId}", TeamDocs.addUserToTeam) {
            val teamService = call.scope.get<TeamService>()
            val teamId = UUID.fromString(call.parameters["teamId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid team ID format")
            val userId = UUID.fromString(call.parameters["userId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")
            
            val requestBody = call.receive<Map<String, String>>()
            val role = requestBody["role"] ?: "PARTICIPANT"

            teamService.addUser(AddUserToTeamCommand(teamId, userId, role))
            call.respond(HttpStatusCode.OK)
        }

        delete("/{teamId}/users/{userId}", TeamDocs.removeUserFromTeam) {
            val teamService = call.scope.get<TeamService>()
            val teamId = UUID.fromString(call.parameters["teamId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid team ID format")
            val userId = UUID.fromString(call.parameters["userId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")

            teamService.removeUser(RemoveUserFromTeamCommand(teamId, userId))
            call.respond(HttpStatusCode.OK)
        }

        get("/{id}/members", TeamDocs.getTeamMembers) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val members = teamService.getTeamMembers(GetTeamMembersCommand(id))
            call.respond(HttpStatusCode.OK, members)
        }

        get("/users/{id}", TeamDocs.getUserTeams) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val teams = teamService.getUserTeams(GetUserTeamsCommand(id))
            call.respond(HttpStatusCode.OK, teams)
        }
    }
} 