package com.hacksync.general.services

import com.hacksync.general.commands.*
import com.hacksync.general.entities.Role
import com.hacksync.general.entities.User
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.utils.PasswordHashing
import org.jdbi.v3.sqlobject.customizer.Bind
import java.util.*

class UserService(private val userRepository: JdbiUserRepository) {
    suspend fun read(command: ReadUserCommand): User {
        return userRepository.getById(command.id)!!
    }

    suspend fun create(command: CreateUserCommand): UUID {
        val user = command.execute();
        userRepository.insert(user)
        return user.id
    }

    suspend fun update(command: UpdateUserCommand) {
        userRepository.update(
            email = command.email,
            name = command.name,
            avatarUrl = command.avatarUrl
        )
    }

    suspend fun delete(command: DeleteUserCommand) {
        userRepository.delete(command.id)
    }

    suspend fun changePassword(command: ChangePasswordCommand) {
        userRepository.updatePassword(
            command.id,
            PasswordHashing.hash(command.password)
        )
    }

    fun getAll(): List<User> {
        return userRepository.getAll()
    }
}