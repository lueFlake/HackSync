package com.hacksync.general

import com.hacksync.general.plugins.configureAuthentication
import com.hacksync.general.plugins.configureDependencies
import com.hacksync.general.plugins.configureSwagger
import com.hacksync.general.plugins.serialization.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDependencies()
    //configureHTTP()
    //configureAdministration()
    configureSerialization()
    configureInjection()
    configureDatabases()
    configureAuthentication()
    //configureMonitoring()
    //configureSecurity()
    configureSwagger()
    configureRouting()
}
