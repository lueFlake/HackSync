package com.hacksync.general

import com.hacksync.general.plugins.configSwagger
import configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //configureHTTP()
    //configureAdministration()
    //configureSockets()
    configureSerialization()
    configureInjection()
    configureDatabases()
    //configureMonitoring()
    //configureSecurity()
    configureRouting()
    configSwagger()
}
