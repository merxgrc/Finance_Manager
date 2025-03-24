// UsersServices.kt
package com.mrxgrc.financemanager.service

import com.mrxgrc.financemanager.model.ExposedUser
import com.mrxgrc.financemanager.model.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class UserService(database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun getAllUsers(): List<ExposedUser> = dbQuery {
        Users.selectAll().map { ExposedUser(it[Users.name], it[Users.age]) }
    }

    suspend fun create(user: ExposedUser): Int = dbQuery {
        Users.insert {
            it[name] = user.name
            it[age] = user.age
        }[Users.id]
    }



    suspend fun read(id: Int): ExposedUser? = dbQuery {
        Users.select { Users.id eq id }
            .map { ExposedUser(it[Users.name], it[Users.age]) }
            .singleOrNull()
    }

    suspend fun update(id: Int, user: ExposedUser) = dbQuery {
        Users.update({ Users.id eq id }) {
            it[name] = user.name
            it[age] = user.age
        }
    }

    suspend fun delete(id: Int) = dbQuery {
        Users.deleteWhere { Users.id eq id }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
