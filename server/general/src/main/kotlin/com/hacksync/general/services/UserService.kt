package com.hacksync.general.services

import com.hacksync.general.commands.*
import com.hacksync.general.entities.User
import com.hacksync.general.exceptions.ValidationException
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.utils.PasswordHashing
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
        val user = userRepository.getById(command.id)!!
        if (!PasswordHashing.verify(command.oldPassword, user.passwordHash))
            throw ValidationException("Password is incorrect")
        userRepository.updatePassword(
            command.id,
            PasswordHashing.hash(command.newPassword)
        )
    }

    suspend fun getAll(): List<User> {
        return userRepository.getAll()
    }

    suspend fun getByEmail(command: ReadUserByEmailCommand): User {
        return userRepository.getByEmail(command.email) ?: throw Exception("User not found")
    }
}