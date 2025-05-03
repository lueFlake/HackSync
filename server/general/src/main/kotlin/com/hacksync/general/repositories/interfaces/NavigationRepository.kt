package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Navigation
import java.util.UUID

interface NavigationRepository {
    suspend fun getAll(): List<Navigation>
    suspend fun getById(id: UUID): Navigation?
    suspend fun insert(navigation: Navigation): Navigation
    suspend fun update(navigation: Navigation)
    suspend fun delete(id: UUID)
} 