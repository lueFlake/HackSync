package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Deadline
import java.util.UUID

interface DeadlineRepository {
    suspend fun getAll(): List<Deadline>
    suspend fun getById(id: UUID): Deadline?
    suspend fun insert(deadline: Deadline): Unit
    suspend fun update(deadline: Deadline): Unit
    suspend fun delete(id: UUID): Unit
} 