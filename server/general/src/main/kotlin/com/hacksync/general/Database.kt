package com.hacksync.general

import com.hacksync.general.mapping.UserMapper
import com.hacksync.general.repositories.JdbiUserRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.koin.dsl.module
import org.koin.ktor.plugin.koin

fun Application.configureDatabases() {
    val dbUrl = environment.config.property("ktor.db.url").getString()
    val dbUser = environment.config.property("ktor.db.user").getString()
    val dbPassword = environment.config.property("ktor.db.password").getString()

    val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = dbUrl
        username = dbUser
        password = dbPassword
        maximumPoolSize = 10
        isAutoCommit = true
        connectionTestQuery = "SELECT 1"
        validate()
    })

    val jdbi = Jdbi.create(dataSource)
        .installPlugin(KotlinPlugin())
        .installPlugin(PostgresPlugin())
        .installPlugin(SqlObjectPlugin())

    jdbi.registerRowMapper(UserMapper())

    koin {
        modules(module {
            single { jdbi }
            single<JdbiUserRepository> { get<Jdbi>().onDemand(JdbiUserRepository::class.java) }
        })
    }
}