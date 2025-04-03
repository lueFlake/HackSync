package com.hacksync.general

import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.hacksync.general.routing.addUserRoutes

fun Application.configureRouting() {
    routing {
        addUserRoutes()
    }
}