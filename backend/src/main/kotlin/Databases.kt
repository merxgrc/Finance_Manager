/*
 * Databases.kt
 * Desc: Establishes connection to database
 * Author: Marcos G.
*/
package com.mrxgrc

import org.jetbrains.exposed.sql.Database

fun connectToPostgres(): Database {
    return Database.connect(
        url = "jdbc:postgresql://localhost:5432/finance_manager",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "Hewhoknowsthyself1_"
    )
}




