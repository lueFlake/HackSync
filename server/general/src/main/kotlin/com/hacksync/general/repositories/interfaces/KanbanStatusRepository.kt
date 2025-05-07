package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.KanbanStatus
import java.util.UUID

interface KanbanStatusRepository {
    fun getAll(): List<KanbanStatus>
    fun getById(id: UUID): KanbanStatus?
    fun insert(status: KanbanStatus)
    fun update(status: KanbanStatus)
    fun delete(id: UUID)
} 