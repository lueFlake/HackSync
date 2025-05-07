package com.hacksync.general

import com.hacksync.general.routing.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond("Hello World!")
        }
        authRoutes()
        addUserRoutes()
        teamRoutes()
        addHackathonRoutes()
        deadlineRoutes()
        addkanbanStatusRoutes()
        addTaskRoutes()
        addLinkRoutes()
        addChatRoutes()
    }
}