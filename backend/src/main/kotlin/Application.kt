/*
 * Application.kt
 * Desc: Main entry point of the application
 * Author: Marcos G.
*/
package com.mrxgrc

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

lateinit var database: Database // global variable

fun main(args: Array<String>) {
    EngineMain.main(args) // Start Netty engine
}

fun Application.module() {
    println("Starting Ktor application...")
    database = connectToPostgres()
    println("Database connected!")

    // Create required tables if they don't exist
    transaction {
        SchemaUtils.create(Users, UserTokens)
    }

    install(CORS) {
        anyHost() // Allows requests from any frontend (for testing)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }

    configureSerialization()
    configureMonitoring()
    configureRouting()


    println("All modules configured, ready to start!")


    environment.monitor.subscribe(ApplicationStopped) {
        println("Application is stopping... Closing database connection.")
    }
}
