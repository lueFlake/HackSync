package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Team
import java.util.UUID

interface TeamRepository {
    suspend fun getAll(): List<Team>
    suspend fun getById(id: UUID): Team?
    suspend fun insert(team: Team): Team
    suspend fun update(team: Team)
    suspend fun delete(id: UUID)
} 