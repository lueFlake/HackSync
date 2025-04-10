package com.hacksync.general.services
import com.hacksync.general.commands.*
import com.hacksync.general.entities.User
import com.hacksync.general.exceptions.ValidationException
import com.hacksync.general.repositories.JdbiTaskRepository
import com.hacksync.general.utils.PasswordHashing
import java.util.*
class TaskService(private val taskRepository: JdbiTaskRepository) {
    suspend fun read(command: ReadUserCommand): User {
        return taskRepository.getById(command.id)!!
    }

    suspend fun create(command: CreateUserCommand): UUID {
        val user = command.execute();
        //taskRepository.insert(user)
        return user.id
    }

    suspend fun update(command: UpdateUserCommand) {
        taskRepository.update(
            email = command.email,
            name = command.name,
            avatarUrl = command.avatarUrl
        )
    }

    suspend fun delete(command: DeleteUserCommand) {
        taskRepository.delete(command.id)
    }

    suspend fun getAll(): List<User> {
        return taskRepository.getAll()
    }
}