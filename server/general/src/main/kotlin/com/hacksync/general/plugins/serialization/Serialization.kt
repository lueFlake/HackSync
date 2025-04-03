import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import com.hacksync.general.plugins.serialization.InstantSerializer
import com.hacksync.general.plugins.serialization.UuidSerializer
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantSerializer)
                contextual(UUID::class, UuidSerializer)
            }
        })
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
