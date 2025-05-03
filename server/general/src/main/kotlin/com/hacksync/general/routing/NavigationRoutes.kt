package com.hacksync.general.routing

import com.hacksync.general.entities.Navigation
import com.hacksync.general.entities.Link
import com.hacksync.general.models.NavigationCreateRequest
import com.hacksync.general.models.NavigationResponse
import com.hacksync.general.services.NavigationService
import com.hacksync.general.docs.NavigationDocs
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

fun Route.navigationRoutes() {
    route("/navigations") {
        val navigationService by inject<NavigationService>()

        get("", NavigationDocs.getAllNavigations) {
            val navigations = navigationService.getAll()
            call.respond(HttpStatusCode.OK, navigations)
        }

        get("/{id}", NavigationDocs.getNavigationById) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val navigation = navigationService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Navigation not found")

            call.respond(HttpStatusCode.OK, navigation)
        }

        post("", NavigationDocs.createNavigation) {
            val request = call.receive<NavigationCreateRequest>()
            val createdNavigation = navigationService.create(request.navigation)
            call.respond(HttpStatusCode.Created, createdNavigation)
        }

        put("/{id}", NavigationDocs.updateNavigation) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            val request = call.receive<NavigationCreateRequest>()
            val updatedNavigation = navigationService.update(request.navigation)
            call.respond(HttpStatusCode.OK, updatedNavigation)
        }

        delete("/{id}", NavigationDocs.deleteNavigation) {
            val id = UUID.fromString(call.parameters["id"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")

            navigationService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
} 