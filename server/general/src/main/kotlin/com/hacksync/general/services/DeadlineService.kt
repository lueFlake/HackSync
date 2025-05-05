package com.hacksync.general.services

import com.hacksync.general.entities.Deadline
import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiDeadlineRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import java.util.UUID

class DeadlineService(
    private val repo: JdbiDeadlineRepository,
    private val linkRepo: JdbiLinkRepository
) {
    suspend fun getAll(): List<Deadline> = repo.getAll()
    
    suspend fun getById(id: UUID): Deadline? = repo.getById(id)
    
    suspend fun create(deadline: Deadline): Deadline {
        // First create the deadline
        repo.insert(deadline)
        
        // Generate and create a link for the deadline
        val link = Link(
            id = UUID.randomUUID(),
            url = "/deadlines/${deadline.id}",
            title = "Deadline: ${deadline.name ?: "Untitled"}",
            entityId = deadline.id,
            entityType = "deadline"
        )
        linkRepo.insert(link)
        
        return deadline
    }
    
    suspend fun update(deadline: Deadline): Deadline {
        repo.update(deadline)
        
        // Update the existing link or create a new one if it doesn't exist
        val existingLink = getLink(deadline.id)
        val link = existingLink ?: Link(
            id = UUID.randomUUID(),
            url = "/deadlines/${deadline.id}",
            title = "Deadline: ${deadline.name ?: "Untitled"}",
            entityId = deadline.id,
            entityType = "deadline"
        )
        
        if (existingLink == null) {
            linkRepo.insert(link)
        } else {
            linkRepo.update(link)
        }

        return deadline
    }
    
    suspend fun delete(id: UUID) {
        // Delete associated link first
        getLink(id)?.let { linkRepo.delete(it.id) }
        // Then delete the deadline
        repo.delete(id)
    }
    
    suspend fun getLink(deadlineId: UUID): Link? = 
        linkRepo.getAll().firstOrNull { it.entityId == deadlineId }
} 