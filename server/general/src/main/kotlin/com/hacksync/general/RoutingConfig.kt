package com.hacksync.general

import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.hacksync.general.routing.addUserRoutes
import com.hacksync.general.routing.authRoutes
import com.hacksync.general.routing.teamRoutes
import com.hacksync.general.routing.navigationRoutes
import com.hacksync.general.routing.deadlineRoutes
import com.hacksync.general.routing.kanbanStatusRoutes
import io.ktor.server.response.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond("Hello World!")
        }
        authRoutes()
        addUserRoutes()
        teamRoutes()
        navigationRoutes()
        deadlineRoutes()
        kanbanStatusRoutes()
    }
}