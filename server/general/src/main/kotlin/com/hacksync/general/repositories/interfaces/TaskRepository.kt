package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Task
import java.util.UUID

interface TaskRepository {
    fun getAll(): List<Task>
    fun getById(id: UUID): Task?
    fun getByUserId(userId: UUID): List<Task>
    fun getByStatusId(statusId: UUID): List<Task>
    fun getByHackathonId(hackathonId: UUID): List<Task>
    fun insert(task: Task): Int
    fun update(task: Task)
    fun delete(id: UUID)
}