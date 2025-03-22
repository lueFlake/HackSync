package com.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement
import java.time.LocalDateTime

enum class Role {
    PARTICIPANT, CAPTAIN, MODERATOR
}

@Serializable
data class User(val id: Int = 0,
                val email: String,
                val passwordHash: String ?= null,
                val role: Role,
                val name: String,
                val avatarUrl: String ?= null,
                @Contextual
                val createdAt: LocalDateTime = LocalDateTime.now(),
                @Contextual
                val updatedAt: LocalDateTime = LocalDateTime.now())

class UserService(private val connection: Connection) {
    companion object {
        private val CREATE_TABLE_USERS = """CREATE TABLE IF NOT EXISTS USERS (
            ID SERIAL PRIMARY KEY, 
            EMAIL VARCHAR(255), 
            PASSWORD_HASH VARCHAR(255), 
            ROLE VARCHAR(20) NOT NULL CHECK (role IN ('PARTICIPANT', 'CAPTAIN', 'MODERATOR')), 
            NAME VARCHAR(255),
            AVATAR_URL VARCHAR(255),
            CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );""".trimIndent()
        private val SELECT_USER_BY_ID = """
            SELECT ID, EMAIL, PASSWORD_HASH, ROLE, NAME, AVATAR_URL, CREATED_AT, UPDATED_AT 
            FROM USERS 
            WHERE ID = ?
        """.trimIndent()
        private val INSERT_USER = """
            INSERT INTO USERS (EMAIL, PASSWORD_HASH, ROLE, NAME, AVATAR_URL) 
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        private val UPDATE_USER_BY_ID = """
            UPDATE USERS 
            SET EMAIL = ?, ROLE = ?, NAME = ?, AVATAR_URL = ?, UPDATED_AT = NOW() 
            WHERE ID = ?
        """.trimIndent()
        private const val DELETE_USER_BY_ID = "DELETE FROM USERS WHERE ID = ?"
    }

    init {
        // statement создается для красоты? 0_0
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USERS)
    }

//    private var newUserId = 0


    // Create new user
    suspend fun create(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.email)
        statement.setString(2, user.passwordHash)
        statement.setString(3, user.role.name)
        statement.setString(4, user.name)
        statement.setString(5, user.avatarUrl)

        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            // мб надо поработать с тем какие именно ошибки прокидывать
            throw Exception("Unable to retrieve the id of the newly inserted user")
        }
    }


    // Read a user by id
    suspend fun read(id: Int): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return@withContext User(
                id           = resultSet.getInt("ID"),
                email        = resultSet.getString("EMAIL"),
                passwordHash = resultSet.getString("PASSWORD_HASH"),
                role         = Role.valueOf(resultSet.getString("ROLE")),
                name         = resultSet.getString("NAME"),
                avatarUrl    = resultSet.getString("AVATAR_URL"),
                createdAt    = resultSet.getTimestamp("CREATED_AT").toLocalDateTime(),
                updatedAt    = resultSet.getTimestamp("UPDATED_AT").toLocalDateTime())
        } else {
            // мб надо поработать с тем какие именно ошибки прокидывать
            throw Exception("User not found")
        }
    }

    // Update a user by id
    suspend fun update(id: Int, user: User) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER_BY_ID)
        statement.setString(1, user.email)
        statement.setString(2, user.role.name)
        statement.setString(3, user.name)
        statement.setString(4, user.avatarUrl)
        statement.setInt(5, id)
        statement.executeUpdate()
    }



    // Delete a user by id
    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER_BY_ID)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}

