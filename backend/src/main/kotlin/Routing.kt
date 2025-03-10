package com.mrxgrc

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.plugins.contentnegotiation.*


fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Server is up!")
        }
    }
}
