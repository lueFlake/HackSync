package com.hacksync.general.plugins

import com.hacksync.general.utils.Environment
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureDependencies(){
    //region Cors
    install(CORS) {
        // 1. Allowed HTTP methods
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Head)

        // 2. Allowed headers
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("X-Requested-With")
        allowHeader("X-Custom-Header")

        // 3. Allowed hosts (any in debug mode, only specific ones in production)
        if (Environment.isDebug) {
            anyHost()
        } else {
            anyHost() // needs to be changed
        }

        // 4. Credentials support (cookies, authentication)
        allowCredentials = true

        // 5. Headers exposed to client in response
        exposeHeader("X-Custom-Header")
        exposeHeader(HttpHeaders.ContentDisposition)

        // 6. Maximum preflight cache time (24 hours)
        maxAgeInSeconds = 86400
    }

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    //endregion

    //region Swagger
    /*routing {
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
    }*/
    //endregion
}