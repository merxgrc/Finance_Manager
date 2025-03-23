/*
 * Models.kt
 * Desc: Defines user table in PostgreSQL
 * Author: Marcos G.
*/
package com.mrxgrc

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class ExposedUser(val name: String, val age: Int)

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", length = 50)
    val age = integer("age")

    override val primaryKey = PrimaryKey(id)
}
