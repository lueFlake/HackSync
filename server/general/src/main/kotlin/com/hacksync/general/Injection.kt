package com.hacksync.general

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.module.requestScope
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.services.UserService
import com.hacksync.general.services.AuthService
import io.ktor.server.engine.*

import io.ktor.server.application.*

fun Application.configureInjection() {
    // Koin
    install(Koin) {
        slf4jLogger()
        modules(module {
            requestScope { scopedOf(::UserService) }
            requestScope { scopedOf(::AuthService) }
            single { environment.config }
        })
    }
}
