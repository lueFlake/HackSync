package com.hacksync.general.services

import com.hacksync.general.commands.team.*
import com.hacksync.general.entities.Team
import com.hacksync.general.entities.Link
import com.hacksync.general.entities.UserTeam
import com.hacksync.general.exceptions.ValidationException
import com.hacksync.general.repositories.JdbiTeamRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import com.hacksync.general.repositories.JdbiUserTeamRepository
import com.hacksync.general.repositories.interfaces.LinkRepository
import com.hacksync.general.repositories.interfaces.TeamRepository
import com.hacksync.general.repositories.interfaces.UserTeamRepository
import java.util.UUID

class TeamService(
    private val repo: TeamRepository,
    private val linkRepo: LinkRepository,
    private val userTeamRepo: UserTeamRepository
) {
    suspend fun getAll(): List<Team> = repo.getAll()
    
    suspend fun read(command: ReadTeamCommand): Team? = repo.getById(command.id)
    
    suspend fun create(command: CreateTeamCommand): Team {
        val team = command.execute()
        // First create the team
        repo.insert(team)
        
        // Generate and create a link for the team
        val link = Link(
            id = UUID.randomUUID(),
            url = "/teams/${team.id}",
            title = "Team: ${team.name}",
            entityId = team.id,
            entityType = "team"
        )
        linkRepo.insert(link)
        
        return team
    }
    
    suspend fun update(command: UpdateTeamCommand): Team {
        val existingTeam = repo.getById(command.id) ?: throw ValidationException("Team not found")
        val updatedTeam = existingTeam.copy(
            name = command.name ?: existingTeam.name,
            updatedAt = java.time.Instant.now()
        )
        repo.update(updatedTeam)
        
        // Update the existing link or create a new one if it doesn't exist
        val existingLink = getLink(command.id)
        val link = existingLink ?: Link(
            id = UUID.randomUUID(),
            url = "/teams/${command.id}",
            title = "Team: ${updatedTeam.name}",
            entityId = command.id,
            entityType = "team"
        )
        
        if (existingLink == null) {
            linkRepo.insert(link)
        } else {
            linkRepo.update(link)
        }
        
        return updatedTeam
    }
    
    suspend fun delete(command: DeleteTeamCommand) {
        // Delete associated link first
        getLink(command.id)?.let { linkRepo.delete(it.id) }
        // Then delete the team
        repo.delete(command.id)
    }
    
    suspend fun getLink(teamId: UUID): Link? = 
        linkRepo.getAll().firstOrNull { it.entityId == teamId }

    // User management methods
    suspend fun addUser(command: AddUserToTeamCommand) {
        userTeamRepo.insert(UserTeam(command.userId, command.teamId))
    }

    suspend fun removeUser(command: RemoveUserFromTeamCommand) {
        userTeamRepo.delete(command.userId, command.teamId)
    }

    suspend fun getTeamMembers(command: GetTeamMembersCommand): List<UUID> {
        return userTeamRepo.getTeamMembers(command.teamId)
    }

    suspend fun getUserTeams(command: GetUserTeamsCommand): List<UUID> {
        return userTeamRepo.getUserTeams(command.userId)
    }
} 