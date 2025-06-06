package com.hacksync.general.routing

import com.hacksync.general.commands.hackathon.CreateHackathonCommand
import com.hacksync.general.commands.hackathon.DeleteHackathonCommand
import com.hacksync.general.commands.hackathon.ReadHackathonCommand
import com.hacksync.general.commands.hackathon.UpdateHackathonCommand
import com.hacksync.general.docs.HackathonDocs
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import com.hacksync.general.services.HackathonService
import java.util.*
import io.github.smiley4.ktoropenapi.*

fun Route.addHackathonRoutes() {
    route("/hackathons") {
        post(HackathonDocs.createHackathon) {
            val command = call.receive<CreateHackathonCommand>()
            val hackathonId = call.scope.get<HackathonService>().create(command)
            call.respond(HttpStatusCode.Created, hackathonId)
        }

        get("/{id}", HackathonDocs.getHackathonById) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val hackathon = call.scope.get<HackathonService>().read(ReadHackathonCommand(id))
            call.respond(HttpStatusCode.OK, hackathon.toDto())
        }

        get("/all", HackathonDocs.getAllHackathons) {
            val hackathons = call.scope.get<HackathonService>().getAll()
            call.respond(hackathons.map { it.toDto() })
            call.respond(HttpStatusCode.OK)
        }

        put("/{id}", HackathonDocs.updateHackathon) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val command = call.receive<UpdateHackathonCommand>()
            call.scope.get<HackathonService>().update(command)
            call.respond(HttpStatusCode.OK, mapOf("success" to true))
        }

        delete("/{id}", HackathonDocs.deleteHackathon) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            call.scope.get<HackathonService>().delete(DeleteHackathonCommand(id))
            call.respond(HttpStatusCode.OK, mapOf("success" to true))
        }
    }
} 