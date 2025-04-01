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
    }

    suspend fun create(user: User): UUID {
        return userRepository.insert {
            name = user.name
            email = user.email
            password = user.passwordHash!!
            role = user.role.toString()
            avatarUrl = user.avatarUrl
        }.id.value
    }

    suspend fun update(id: UUID, user: User) {
        userRepository.update(id) {
            name = user.name
            email = user.email
            avatarUrl = user.avatarUrl
        }
    }

    suspend fun delete(id: UUID): Boolean {
        return userRepository.delete(id)
    }
}