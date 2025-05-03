package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.UserTeam
import java.util.UUID

interface UserTeamRepository {
    suspend fun getAll(): List<UserTeam>
    suspend fun getById(userId: UUID, teamId: UUID): UserTeam?
    suspend fun insert(userTeam: UserTeam)
    suspend fun update(userTeam: UserTeam)
    suspend fun delete(userId: UUID, teamId: UUID)
} 