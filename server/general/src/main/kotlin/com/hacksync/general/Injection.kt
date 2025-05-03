package com.hacksync.general

import com.hacksync.general.repositories.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.module.requestScope
import com.hacksync.general.services.UserService
import com.hacksync.general.services.AuthService
import io.ktor.server.engine.*

import io.ktor.server.application.*
import org.jdbi.v3.core.Jdbi

fun Application.configureInjection() {
    // Koin
    install(Koin) {
        slf4jLogger()
        modules(module {
            requestScope { scopedOf(::UserService) }
            requestScope { scopedOf(::AuthService) }
            single { environment.config }
            single<JdbiUserRepository> { get<Jdbi>().onDemand(JdbiUserRepository::class.java) }
            single<JdbiKanbanStatusRepository> { get<Jdbi>().onDemand(JdbiKanbanStatusRepository::class.java) }
            single<JdbiUserTeamRepository> { get<Jdbi>().onDemand(JdbiUserTeamRepository::class.java) }
            single<JdbiNavigationRepository> { get<Jdbi>().onDemand(JdbiNavigationRepository::class.java) }
            single<JdbiDeadlineRepository> { get<Jdbi>().onDemand(JdbiDeadlineRepository::class.java) }
            single<JdbiTeamRepository> { get<Jdbi>().onDemand(JdbiTeamRepository::class.java) }
            single<JdbiLinkRepository> { get<Jdbi>().onDemand(JdbiLinkRepository::class.java) }
        })
    }
}
