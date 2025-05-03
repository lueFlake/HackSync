package com.hacksync.general.services

import com.hacksync.general.entities.Team
import com.hacksync.general.entities.Link
import com.hacksync.general.entities.UserTeam
import com.hacksync.general.repositories.JdbiTeamRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import com.hacksync.general.repositories.JdbiUserTeamRepository
import java.util.UUID

class TeamService(
    private val repo: JdbiTeamRepository,
    private val linkRepo: JdbiLinkRepository,
    private val userTeamRepo: JdbiUserTeamRepository
) {
    suspend fun getAll(): List<Team> = repo.getAll()
    
    suspend fun getById(id: UUID): Team? = repo.getById(id)
    
    suspend fun create(team: Team): Team {
        // First create the team
        val createdTeam = repo.insert(team)
        
        // Generate and create a link for the team
        val link = Link(
            id = UUID.randomUUID(),
            url = "/teams/${createdTeam.id}",
            title = "Team: ${createdTeam.name}",
            entityId = createdTeam.id,
            entityType = "team"
        )
        linkRepo.insert(link)
        
        return createdTeam
    }
    
    suspend fun update(team: Team): Team {
        repo.update(team)
        
        // Update the existing link or create a new one if it doesn't exist
        val existingLink = getLink(team.id)
        val link = existingLink ?: Link(
            id = UUID.randomUUID(),
            url = "/teams/${team.id}",
            title = "Team: ${team.name}",
            entityId = team.id,
            entityType = "team"
        )
        
        if (existingLink == null) {
            linkRepo.insert(link)
        } else {
            linkRepo.update(link)
        }
        
        return team
    }
    
    suspend fun delete(id: UUID) {
        // Delete associated link first
        getLink(id)?.let { linkRepo.delete(it.id) }
        // Then delete the team
        repo.delete(id)
    }
    
    suspend fun getLink(teamId: UUID): Link? = 
        linkRepo.getAll().firstOrNull { it.entityId == teamId }

    // User management methods
    suspend fun addUser(teamId: UUID, userId: UUID) {
        userTeamRepo.insert(UserTeam(userId, teamId))
    }

    suspend fun removeUser(teamId: UUID, userId: UUID) {
        userTeamRepo.delete(userId, teamId)
    }

    suspend fun getTeamMembers(teamId: UUID): List<UUID> {
        return userTeamRepo.getAll()
            .filter { it.teamId == teamId }
            .map { it.userId }
    }

    suspend fun getUserTeams(userId: UUID): List<UUID> {
        return userTeamRepo.getAll()
            .filter { it.userId == userId }
            .map { it.teamId }
    }
} 