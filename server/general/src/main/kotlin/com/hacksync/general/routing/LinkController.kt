package com.hacksync.general.routing

import com.hacksync.general.entities.Link
import com.hacksync.general.models.LinkWithEntity
import com.hacksync.general.services.LinkService
import com.hacksync.general.docs.LinkDocs
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import io.github.smiley4.ktoropenapi.*
import io.github.smiley4.ktoropenapi.config.RouteConfig
import java.util.UUID

fun Route.addLinkRoutes() {
    route("/links") {
        get("", LinkDocs.getAllLinks) {
            val links = call.scope.get<LinkService>().getAll()
            call.respond(HttpStatusCode.OK, links.map { it.toDto() })
        }

        get("/{id}", LinkDocs.getLinkById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val link = call.scope.get<LinkService>().getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Link not found")

            call.respond(HttpStatusCode.OK, link.toDto())
        }

        get("/search", LinkDocs.searchLinks) {
            val title = call.request.queryParameters["title"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "Title parameter is required")
                return@get
            }
            
            val links = call.scope.get<LinkService>().getByTitle(title)
            call.respond(HttpStatusCode.OK, links)
        }
    }
}