package com.hacksync.general.plugins

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSwagger() {
    install(OpenApi) {
        security {
            securityScheme("jwtToken") {
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                bearerFormat = "JWT"
                description = "JWT token for authentication"
            }
            defaultSecuritySchemeNames("jwtToken")
            defaultUnauthorizedResponse {
                description = "Unauthorized access - Invalid or missing JWT token"
            }
        }
        schemas {
            generator = SchemaGenerator.reflection {
                overwrite(SchemaGenerator.TypeOverwrites.File())
            }
        }
        info {
            title = "HackSync API"
            version = "1.0.0"
            description = "API documentation for HackSync"
            contact {
                name = "HackSync Team"
                email = "contact@hacksync.com"
            }
        }
        server {
            url = "http://localhost:8080"
            description = "Development Server"
        }
        server {
            url = "https://api.hacksync.com/"
            description = "Production Server"
        }
        routing {
            route("api.json") {
                openApi()
            }
            route("swagger") {
                swaggerUI("/api.json")
            }
            get {
                call.respondRedirect("/swagger/index.html?url=/api.json", true)
            }
        }
    }
}