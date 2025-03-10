package com.mrxgrc

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database

lateinit var database: Database // ✅ Declare global variable

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    println("Starting Ktor application...") // ✅ Debugging
    database = connectToPostgres()
    println("Database connected!") // ✅ Debugging

    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureRouting()

    println("All modules configured, ready to start!") // ✅ Debugging

    environment.monitor.subscribe(ApplicationStopped) {
        println("Application is stopping... Closing database connection.")

    }
}

