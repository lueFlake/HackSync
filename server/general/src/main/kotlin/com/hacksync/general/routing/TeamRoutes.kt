package com.hacksync.general.routing

import com.hacksync.general.entities.Team
import com.hacksync.general.entities.Link
import com.hacksync.general.models.TeamCreateRequest
import com.hacksync.general.models.TeamResponse
import com.hacksync.general.services.TeamService
import com.hacksync.general.docs.TeamDocs
import com.hacksync.general.utils.jsonBody
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
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
            call.respond(HttpStatusCode.OK, teams)
        }

        get("/{id}", TeamDocs.getTeamById) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val team = teamService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Team not found")

            call.respond(HttpStatusCode.OK, team)
        }

        post("", TeamDocs.createTeam) {
            val teamService = call.scope.get<TeamService>()
            val request = call.receive<TeamCreateRequest>()
            val createdTeam = teamService.create(request.team)
            call.respond(HttpStatusCode.Created, createdTeam)
        }

        put("/{id}", TeamDocs.updateTeam) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val request = call.receive<TeamCreateRequest>()
            val updatedTeam = teamService.update(request.team)
            call.respond(HttpStatusCode.OK, updatedTeam)
        }

        delete("/{id}", TeamDocs.deleteTeam) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            teamService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }

        post("/{teamId}/users/{userId}", TeamDocs.addUserToTeam) {
            val teamService = call.scope.get<TeamService>()
            val teamId = UUID.fromString(call.parameters["teamId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid team ID format")
            val userId = UUID.fromString(call.parameters["userId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")

            teamService.addUser(teamId, userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{teamId}/users/{userId}", TeamDocs.removeUserFromTeam) {
            val teamService = call.scope.get<TeamService>()
            val teamId = UUID.fromString(call.parameters["teamId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid team ID format")
            val userId = UUID.fromString(call.parameters["userId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid user ID format")

            teamService.removeUser(teamId, userId)
            call.respond(HttpStatusCode.OK)
        }

        get("/{id}/members", TeamDocs.getTeamMembers) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val members = teamService.getTeamMembers(id)
            call.respond(HttpStatusCode.OK, members)
        }

        get("/users/{id}", TeamDocs.getUserTeams) {
            val teamService = call.scope.get<TeamService>()
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val teams = teamService.getUserTeams(id)
            call.respond(HttpStatusCode.OK, teams)
        }
    }
} 