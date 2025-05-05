package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Link
import java.util.UUID

interface LinkRepository {
    suspend fun getAll(): List<Link>
    suspend fun getById(id: UUID): Link?
    suspend fun insert(link: Link): Unit
    suspend fun update(link: Link): Unit
    suspend fun delete(id: UUID): Unit
} 