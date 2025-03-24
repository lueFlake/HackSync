import com.papsign.ktor.openapigen.OpenAPIGen
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureAdministration()
    configureSockets()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureRouting()
    configureInjection()
}
