// TokenRepository.kt
package com.mrxgrc

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object TokenRepository {

    fun storeAccessToken(userId: Int, accessToken: String): Int? = transaction {
        try {
            UserTokens.insert {
                it[UserTokens.userId] = userId
                it[UserTokens.accessToken] = accessToken
            } get UserTokens.id
        } catch (e: Exception) {
            println("❌ Error storing access token: ${e.message}")
            null
        }
    }

    fun getAccessToken(userId: Int): String? = transaction {
        UserTokens.select { UserTokens.userId eq userId }
            .map { it[UserTokens.accessToken] }
            .firstOrNull()
            .also { token ->
                if (token == null) {
                    println("⚠️ Warning: No access token found for user $userId")
                } else {
                    println("✅ Retrieved access token for user $userId")
                }
            }
    }
}

