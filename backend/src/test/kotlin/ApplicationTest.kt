package com.mrxgrc

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.sql.DriverManager.println
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application { configureRouting() }
        val client = createClient { install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) }
        val response = client.get("/")
        // Debugging output
        println("Response status: ${response.status}")
        println("Response body: ${response.bodyAsText()}")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
