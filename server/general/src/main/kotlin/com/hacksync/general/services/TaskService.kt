package com.hacksync.general.services

import com.hacksync.general.commands.task.CreateTaskCommand
import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.entities.Task
import com.hacksync.general.entities.Link
import com.hacksync.general.exceptions.ValidationException
import com.hacksync.general.repositories.JdbiTaskRepository
import com.hacksync.general.repositories.UserRepository
import com.hacksync.general.repositories.interfaces.KanbanStatusRepository
import com.hacksync.general.repositories.interfaces.TaskRepository
import com.hacksync.general.repositories.interfaces.LinkRepository
import java.time.Instant
import java.util.UUID

class TaskService(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val kanbanStatusRepository: KanbanStatusRepository,
    private val linkRepository: LinkRepository
) {
    fun getAll(): List<Task> = taskRepository.getAll()
    
    fun getById(id: UUID): Task? = taskRepository.getById(id)
    
    fun getByUserId(userId: UUID): List<Task> = taskRepository.getByUserId(userId)
    
    fun getByStatusId(statusId: UUID): List<Task> = taskRepository.getByStatusId(statusId)
    
    fun create(command: CreateTaskCommand): Task {
        if (userRepository.getById(command.userId!!) == null)
            throw ValidationException("Incorrect user id")

        val backlogStatus = kanbanStatusRepository.getById(CreateTaskCommand.DEFAULT_BACKLOG_STATUS_ID)
            ?: throw IllegalStateException("Default Backlog status not found")
            
        val task = command.execute(backlogStatus.id)

        // Create a link for the task
        val link = Link(
            id = UUID.randomUUID(),
            url = "/tasks/${task.id}",
            title = "Task: ${task.name}",
            entityId = task.id,
            entityType = "task"
        )
        linkRepository.insert(link)

        // Update the task with the link ID
        val updatedTask = task.copy(linkId = link.id)
        taskRepository.update(updatedTask)

        taskRepository.insert(task)

        val saved = taskRepository.getById(task.id)!! // now has serial
        val number = "TASK-${saved.serial}"

        val updated = saved.copy(number = number)
        taskRepository.update(updated)
        
        return updatedTask
    }
    
    fun update(task: Task) {
        taskRepository.update(task)
    }
    
    fun delete(id: UUID) {
        // Delete the associated link first
        val task = taskRepository.getById(id) ?: return
        task.linkId?.let { linkId ->
            linkRepository.delete(linkId)
        }
        taskRepository.delete(id)
    }

    suspend fun getLink(taskId: UUID): Link? =
        linkRepository.getAll().firstOrNull { it.entityId == taskId }
}