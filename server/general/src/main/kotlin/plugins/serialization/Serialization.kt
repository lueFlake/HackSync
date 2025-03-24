import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import plugins.serialization.LocalDateTimeSerializer
import java.time.LocalDateTime

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(LocalDateTime::class, LocalDateTimeSerializer)
            }
        })
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
