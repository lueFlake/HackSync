package com.hacksync.general.services

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiKanbanStatusRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import com.hacksync.general.repositories.interfaces.KanbanStatusRepository
import com.hacksync.general.repositories.interfaces.LinkRepository
import java.time.Instant
import java.util.UUID

class KanbanStatusService(
    private val repo: KanbanStatusRepository,
    private val linkRepo: LinkRepository
) {
    suspend fun getAll(): List<KanbanStatus> = repo.getAll()
    
    suspend fun getById(id: UUID): KanbanStatus? = repo.getById(id)
    
    suspend fun create(status: KanbanStatus): KanbanStatus {
        val now = Instant.now()
        val statusWithTimestamps = status.copy(
            createdAt = now,
            updatedAt = now
        )
        
        // First create the status
        repo.insert(statusWithTimestamps)
        
        // Generate and create a link for the status
        val link = Link(
            id = UUID.randomUUID(),
            url = "/kanban-statuses/${status.id}",
            title = "Status: ${status.name}",
            entityId = status.id,
            entityType = "kanban_status"
        )
        linkRepo.insert(link)
        
        return statusWithTimestamps
    }
    
    suspend fun update(status: KanbanStatus): KanbanStatus {
        val statusWithUpdatedTimestamp = status.copy(
            updatedAt = Instant.now()
        )
        repo.update(statusWithUpdatedTimestamp)
        
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
        
        return statusWithUpdatedTimestamp
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