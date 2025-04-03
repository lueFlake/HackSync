package com.hacksync.general

import io.ktor.server.application.*
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.module.requestScope
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.services.UserService
import org.jdbi.v3.core.Jdbi

fun Application.configureInjection() {
    // Koin
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

val appModule = module {
    requestScope { scopedOf(::UserService) }
}