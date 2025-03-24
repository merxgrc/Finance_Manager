// UserTokens.kt
package com.mrxgrc.financemanager.model

import org.jetbrains.exposed.sql.Table

object UserTokens : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)  // Ensure Users table exists
    val accessToken = varchar("access_token", 255)
    override val primaryKey = PrimaryKey(id)
}