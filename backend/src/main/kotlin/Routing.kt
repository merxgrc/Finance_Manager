// Routing.kt
package com.mrxgrc

import com.mrxgrc.PlaidService.gson
import com.plaid.client.model.AccountBase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class GetAccountsRequest(val userId: Int)

@Serializable
data class GetTransactionsRequest(val userId: Int, val startDate: String, val endDate: String)

@Serializable
data class CreateLinkTokenRequest(val userId: Int)  // 🔹 Changed from String to Int

@Serializable
data class ExchangePublicTokenRequest(val userId: Int, val publicToken: String)

fun Application.configureRouting() {
    val userService = UserService(database)

    routing {

        post("/plaid/get_accounts") {
            val req = call.receive<GetAccountsRequest>()
            val accessToken = TokenRepository.getAccessToken(req.userId)
            if (accessToken == null) {
                call.respond(HttpStatusCode.OK, emptyList<AccountBase>())
                return@post
            }
            val accounts = PlaidService.getAccounts(accessToken)
            call.respondText(gson.toJson(accounts ?: "No accounts found"), ContentType.Application.Json)
        }

        post("/plaid/get_transactions") {
            val req = call.receive<GetTransactionsRequest>()
            val accessToken = TokenRepository.getAccessToken(req.userId)
            if (accessToken == null) {
                call.respond(HttpStatusCode.NotFound, "Access token not found for user ${req.userId}")
                return@post
            }
            val transactions = PlaidService.getTransactions(accessToken, req.startDate, req.endDate)
            call.respond(HttpStatusCode.OK, transactions ?: "No transactions found")
        }

        post("/plaid/create_link_token") {
            val requestPayload = call.receive<CreateLinkTokenRequest>()
            val linkToken = PlaidService.createLinkToken(requestPayload.userId.toString())  // 🔹 Convert to String
            call.respond(HttpStatusCode.OK, mapOf("link_token" to linkToken))
        }

        post("/plaid/exchange_public_token") {
            val requestPayload = call.receive<ExchangePublicTokenRequest>()
            val accessToken = PlaidService.exchangePublicToken(requestPayload.userId, requestPayload.publicToken)  // 🔹 Pass `userId`
            if (accessToken != null) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Access token exchanged and stored successfully"))
            } else {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to exchange public token"))
            }
        }

        get("/") {
            call.respondText("Server is up!")
        }

        route("/users") {
            post {
                val user = call.receive<ExposedUser>()
                val id = userService.create(user)
                call.respond(HttpStatusCode.Created, id)
            }

            get {
                val users = userService.getAllUsers()
                call.respond(HttpStatusCode.OK, users)
            }

            get("{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = userService.read(id)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            put("{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = call.receive<ExposedUser>()
                userService.update(id, user)
                call.respond(HttpStatusCode.OK)
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                userService.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
