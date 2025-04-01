import io.ktor.server.application.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.module.requestScope
import repositories.UserRepository
import repositories.UserTable
import services.UserService

fun Application.configureInjection() {
    // Koin
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

val appModule = module {
    requestScope { scopedOf(::UserService) }
    singleOf(::UserRepository) { bind<UserRepository>() }
}