package com.hacksync.general

import com.hacksync.general.plugins.configureAuthentication
import com.hacksync.general.plugins.configureDependencies
import com.hacksync.general.plugins.configureSwagger
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
    configureAuthentication()
    //configureMonitoring()
    //configureSecurity()
    configureDependencies()
    configureSwagger()
    configureRouting()
}
