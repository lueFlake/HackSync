package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.KanbanStatus
import java.util.UUID

interface KanbanStatusRepository {
    suspend fun getAll(): List<KanbanStatus>
    suspend fun getById(id: UUID): KanbanStatus?
    suspend fun insert(status: KanbanStatus): KanbanStatus
    suspend fun update(status: KanbanStatus)
    suspend fun delete(id: UUID)
} 