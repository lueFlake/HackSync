package services

import entities.Role
import entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.ktor.plugin.Koin
import org.koin.dsl.module
import repositories.UserDAO
import repositories.UserRepository
import repositories.UserTable.role
import java.sql.Connection
import java.sql.Statement
import java.util.*

class UserService(private val userRepository: UserRepository) {
    // Read a user by id
    suspend fun read(id: UUID): User {
        val dao = userRepository.getById(id);
        return User(
            id = dao?.id?.value!!,
            name = dao.name,
            email = dao.email,
            role = Role.valueOf(dao.role)
        )
        /*val statement = connection.prepareStatement(SELECT_USER_BY_ID)
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
            throw Exception("User nottt found")
        }*/
    }


    // Create new user
    suspend fun create(user: User): UUID {
        return userRepository.insert {
            name = user.name
            email = user.email
            password = user.passwordHash!!
            role = user.role.toString()
            avatarUrl = user.avatarUrl
        }.id.value
        /*val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
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
        }*/
    }

    // Update a user by id
    suspend fun update(id: UUID, user: User) {
        userRepository.update(id) {
            name = user.name
            email = user.email
            avatarUrl = user.avatarUrl
        }
        /*
        val statement = connection.prepareStatement(UPDATE_USER_BY_ID)
        statement.setString(1, user.email)
        statement.setString(2, user.role.name)
        statement.setString(3, user.name)
        statement.setString(4, user.avatarUrl)
        statement.setInt(5, id)
        statement.executeUpdate()*/
    }

    // Delete a user by id
    suspend fun delete(id: UUID): Boolean {
        return userRepository.delete(id)
        /*
        val statement = connection.prepareStatement(DELETE_USER_BY_ID)
        statement.setInt(1, id)
        statement.executeUpdate()*/
    }
}