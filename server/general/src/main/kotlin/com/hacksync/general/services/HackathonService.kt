package com.hacksync.general.services

import com.hacksync.general.entities.Hackathon
import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiHackathonRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import java.util.UUID

class HackathonService(
    private val repo: JdbiHackathonRepository,
    private val linkRepo: JdbiLinkRepository
) {
    suspend fun getAll(): List<Hackathon> = repo.getAll()
    
    suspend fun getById(id: UUID): Hackathon? = repo.getById(id)
    
    suspend fun create(hackathon: Hackathon): Hackathon {
        // First create the hackathon
        val createdHackathon = repo.insert(hackathon)
        
        // Generate and create a link for the hackathon
        val link = Link(
            id = UUID.randomUUID(),
            url = "/hackathons/${createdHackathon.id}",
            title = "Hackathon: ${createdHackathon.name}",
            entityId = createdHackathon.id,
            entityType = "hackathon"
        )
        linkRepo.insert(link)
        
        return createdHackathon
    }
    
    suspend fun update(hackathon: Hackathon): Hackathon {
        repo.update(hackathon)
        
        // Update the existing link or create a new one if it doesn't exist
        val existingLink = getLink(hackathon.id)
        val link = existingLink ?: Link(
            id = UUID.randomUUID(),
            url = "/hackathons/${hackathon.id}",
            title = "Hackathon: ${hackathon.name}",
            entityId = hackathon.id,
            entityType = "hackathon"
        )
        
        if (existingLink == null) {
            linkRepo.insert(link)
        } else {
            linkRepo.update(link)
        }
        
        return hackathon
    }
    
    suspend fun delete(id: UUID) {
        // Delete associated link first
        getLink(id)?.let { linkRepo.delete(it.id) }
        // Then delete the hackathon
        repo.delete(id)
    }
    
    suspend fun getLink(hackathonId: UUID): Link? = 
        linkRepo.getAll().firstOrNull { it.entityId == hackathonId }
} 