package com.hacksync.general.routing

import com.hacksync.general.entities.Hackathon
import com.hacksync.general.entities.Link
import com.hacksync.general.models.HackathonCreateRequest
import com.hacksync.general.models.HackathonResponse
import com.hacksync.general.services.HackathonService
import com.hacksync.general.docs.HackathonDocs
import com.hacksync.general.utils.jsonBody
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
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.LocalDate

fun Route.hackathonRoutes() {
    route("/hackathons") {
        val hackathonService by inject<HackathonService>()

        get("", HackathonDocs.getAllHackathons) {
            val hackathons = hackathonService.getAll()
            call.respond(HttpStatusCode.OK, hackathons)
        }

        get("/{id}", HackathonDocs.getHackathonById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val hackathon = hackathonService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Hackathon not found")

            call.respond(HttpStatusCode.OK, hackathon)
        }

        post("", HackathonDocs.createHackathon) {
            val request = call.receive<HackathonCreateRequest>()
            val createdHackathon = hackathonService.create(request.hackathon)
            call.respond(HttpStatusCode.Created, createdHackathon)
        }

        put("/{id}", HackathonDocs.updateHackathon) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val request = call.receive<HackathonCreateRequest>()
            val updatedHackathon = hackathonService.update(request.hackathon)
            call.respond(HttpStatusCode.OK, updatedHackathon)
        }

        delete("/{id}", HackathonDocs.deleteHackathon) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            hackathonService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 