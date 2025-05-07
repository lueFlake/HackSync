package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.UserTeam
import java.util.UUID

interface UserTeamRepository {
    fun getAll(): List<UserTeam>
    fun getById(userId: UUID, teamId: UUID): UserTeam?
    fun insert(userTeam: UserTeam)
    fun update(userTeam: UserTeam)
    fun delete(userId: UUID, teamId: UUID)
    fun getTeamMembers(teamId: UUID): List<UUID>
    fun getUserTeams(userId: UUID): List<UUID>
}