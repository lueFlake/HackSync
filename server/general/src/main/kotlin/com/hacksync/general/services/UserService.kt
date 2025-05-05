package com.hacksync.general.services

import com.hacksync.general.commands.*
import com.hacksync.general.entities.User
import com.hacksync.general.entities.Link
import com.hacksync.general.exceptions.ValidationException
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import com.hacksync.general.utils.PasswordHashing
import java.util.*

class UserService(
    private val userRepository: JdbiUserRepository,
    private val linkRepository: JdbiLinkRepository
) {
    suspend fun read(command: ReadUserCommand): User {
        return userRepository.getById(command.id)!!
    }

    suspend fun create(command: CreateUserCommand): UUID {
        val user = command.execute();
        userRepository.insert(user)
        
        // Generate and create a link for the user
        val link = Link(
            id = UUID.randomUUID(),
            url = "/users/${user.id}",
            title = "User: ${user.name}",
            entityId = user.id,
            entityType = "user"
        )
        linkRepository.insert(link)
        
        return user.id
    }

    suspend fun update(command: UpdateUserCommand) {
        userRepository.update(
            email = command.email,
            name = command.name,
            avatarUrl = command.avatarUrl
        )
        
        // Update the existing link or create a new one if it doesn't exist
        getLink(command.id)?.let { linkRepository.delete(it.id) }
        
        val link = Link(
            id = UUID.randomUUID(),
            url = "/users/${command.id}",
            title = "User: ${command.name}",
            entityId = command.id,
            entityType = "user"
        )
        linkRepository.insert(link)
    }

    suspend fun delete(command: DeleteUserCommand) {
        // Delete associated link first
        getLink(command.id)?.let { linkRepository.delete(it.id) }
        // Then delete the user
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

    suspend fun getAll(): List<User> = userRepository.getAll()

    suspend fun getByEmail(command: ReadUserByEmailCommand): User {
        return userRepository.getByEmail(command.email) ?: throw Exception("User not found")
    }

    suspend fun getLink(userId: UUID): Link? =
        linkRepository.getAll().firstOrNull { it.entityId == userId }
}