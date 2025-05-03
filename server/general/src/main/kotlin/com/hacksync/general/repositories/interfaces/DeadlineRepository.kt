package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Deadline
import java.util.UUID

interface DeadlineRepository {
    suspend fun getAll(): List<Deadline>
    suspend fun getById(id: UUID): Deadline?
    suspend fun insert(deadline: Deadline): Deadline
    suspend fun update(deadline: Deadline)
    suspend fun delete(id: UUID)
} 