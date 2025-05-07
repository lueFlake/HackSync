package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Deadline
import java.util.UUID

interface DeadlineRepository {
    fun getAll(): List<Deadline>
    fun getById(id: UUID): Deadline?
    fun insert(deadline: Deadline)
    fun update(deadline: Deadline)
    fun delete(id: UUID)
} 