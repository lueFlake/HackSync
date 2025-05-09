package com.hacksync.general

import com.hacksync.general.repositories.*
import com.hacksync.general.repositories.interfaces.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.module.requestScope
import com.hacksync.general.services.*
import io.ktor.server.engine.*
import io.ktor.server.application.*
import org.jdbi.v3.core.Jdbi

fun Application.configureInjection() {
    // Koin
    install(Koin) {
        slf4jLogger()
        modules(module {
            // Services
            requestScope { scopedOf(::UserService) }
            requestScope { scopedOf(::AuthService) }
            requestScope { scopedOf(::TaskService) }
            requestScope { scopedOf(::LinkService) }
            requestScope { scopedOf(::DeadlineService) }
            requestScope { scopedOf(::KanbanStatusService) }
            requestScope { scopedOf(::HackathonService) }
            requestScope { scopedOf(::TeamService) }
            single { ChatService(get()) }

            // Config
            single { environment.config }

            // Repositories
            single<UserRepository> { get<Jdbi>().onDemand(JdbiUserRepository::class.java) }
            single<KanbanStatusRepository> { get<Jdbi>().onDemand(JdbiKanbanStatusRepository::class.java) }
            single<UserTeamRepository> { get<Jdbi>().onDemand(JdbiUserTeamRepository::class.java) }
            single<HackathonRepository> { get<Jdbi>().onDemand(JdbiHackathonRepository::class.java) }
            single<DeadlineRepository> { get<Jdbi>().onDemand(JdbiDeadlineRepository::class.java) }
            single<TeamRepository> { get<Jdbi>().onDemand(JdbiTeamRepository::class.java) }
            single<LinkRepository> { get<Jdbi>().onDemand(JdbiLinkRepository::class.java) }
            single<TaskRepository> { get<Jdbi>().onDemand(JdbiTaskRepository::class.java) }
            single<MessageRepository> { JdbiMessageRepository(get()) }
            single<RevokedTokenRepository> { get<Jdbi>().onDemand(JdbiRevokedTokenRepository::class.java) }
        })
    }
}
