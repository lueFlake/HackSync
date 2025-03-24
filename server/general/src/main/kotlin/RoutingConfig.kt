import io.ktor.server.routing.*
import io.ktor.server.application.*
import routing.addUserRoutes

fun Application.configureRouting() {
    routing {
        addUserRoutes()
    }
}