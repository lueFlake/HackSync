package com.hacksync.general

import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.hacksync.general.routing.addUserRoutes
import io.ktor.server.plugins.openapi.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
        get("/") {
            call.respond("Hello World!")
        }
        addUserRoutes()
    }
}