package com.hacksync.general.services

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiKanbanStatusRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import java.util.UUID

class KanbanStatusService(
    private val repo: JdbiKanbanStatusRepository,
    private val linkRepo: JdbiLinkRepository
) {
    suspend fun getAll(): List<KanbanStatus> = repo.getAll()
    
    suspend fun getById(id: UUID): KanbanStatus? = repo.getById(id)
    
    suspend fun create(status: KanbanStatus): KanbanStatus {
        // First create the status
        repo.insert(status)
        
        // Generate and create a link for the status
        val link = Link(
            id = UUID.randomUUID(),
            url = "/kanban-statuses/${status.id}",
            title = "Status: ${status.name}",
            entityId = status.id,
            entityType = "kanban_status"
        )
        linkRepo.insert(link)
        
        return status
    }
    
    suspend fun update(status: KanbanStatus): KanbanStatus {
        repo.update(status)
        
        // Update the existing link or create a new one if it doesn't exist
        val existingLink = getLink(status.id)
        val link = existingLink ?: Link(
            id = UUID.randomUUID(),
            url = "/kanban-statuses/${status.id}",
            title = "Status: ${status.name}",
            entityId = status.id,
            entityType = "kanban_status"
        )
        
        if (existingLink == null) {
            linkRepo.insert(link)
        } else {
            linkRepo.update(link)
        }
        
        return status
    }
    
    suspend fun delete(id: UUID) {
        // Delete associated link first
        getLink(id)?.let { linkRepo.delete(it.id) }
        // Then delete the status
        repo.delete(id)
    }
    
    suspend fun getLink(statusId: UUID): Link? = 
        linkRepo.getAll().firstOrNull { it.entityId == statusId }
} 