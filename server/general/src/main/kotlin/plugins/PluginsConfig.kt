package plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configSwagger(){
    //region Cors
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        anyHost() // @TODO: Только в дебаге
    }
    //endregion

    //region Swagger
    routing {
        openAPI(path = "openapi")
    }
    routing {
        swaggerUI(path = "swagger") {
            version = "4.15.5"
        }
    }
    //endregion
}