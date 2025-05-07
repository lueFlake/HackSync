package com.hacksync.general.services

import com.hacksync.general.commands.task.CreateTaskCommand
import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.entities.Task
import com.hacksync.general.repositories.interfaces.KanbanStatusRepository
import com.hacksync.general.repositories.interfaces.TaskRepository
import java.time.Instant
import java.util.UUID

class TaskService(
    private val taskRepository: TaskRepository,
    private val kanbanStatusRepository: KanbanStatusRepository
) {
    fun getAll(): List<Task> = taskRepository.getAll()
    
    fun getById(id: UUID): Task? = taskRepository.getById(id)
    
    fun getByUserId(userId: UUID): List<Task> = taskRepository.getByUserId(userId)
    
    fun getByStatusId(statusId: UUID): List<Task> = taskRepository.getByStatusId(statusId)
    
    fun create(command: CreateTaskCommand): Task {
        val backlogStatus = kanbanStatusRepository.getById(CreateTaskCommand.DEFAULT_BACKLOG_STATUS_ID)
            ?: throw IllegalStateException("Default Backlog status not found")
            
        val task = command.execute().copy(status = backlogStatus)
        taskRepository.insert(task)
        return task
    }
    
    fun update(task: Task) {
        taskRepository.update(task)
    }
    
    fun delete(id: UUID) {
        taskRepository.delete(id)
    }
}