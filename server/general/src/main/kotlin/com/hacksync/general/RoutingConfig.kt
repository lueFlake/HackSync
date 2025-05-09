package com.hacksync.general

import com.hacksync.general.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.readByteArray
import java.io.File
import java.time.Instant
import java.time.LocalDate
import kotlin.io.use

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
        staticFiles("/uploads", File("uploads"))
    }
}