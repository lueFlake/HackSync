package com.hacksync.general.services

import com.hacksync.general.entities.Navigation
import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiNavigationRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import java.util.UUID

class NavigationService(
    private val repo: JdbiNavigationRepository,
    private val linkRepo: JdbiLinkRepository
) {
    suspend fun getAll(): List<Navigation> = repo.getAll()
    
    suspend fun getById(id: UUID): Navigation? = repo.getById(id)
    
    suspend fun create(navigation: Navigation): Navigation {
        // First create the navigation
        val createdNavigation = repo.insert(navigation)
        
        // Generate and create a link for the navigation
        val link = Link(
            id = UUID.randomUUID(),
            url = "/navigations/${createdNavigation.id}",
            title = "Navigation: ${createdNavigation.name}",
            entityId = createdNavigation.id,
            entityType = "navigation"
        )
        linkRepo.insert(link)
        
        return createdNavigation
    }
    
    suspend fun update(navigation: Navigation): Navigation {
        repo.update(navigation)
        
        // Update the existing link or create a new one if it doesn't exist
        val existingLink = getLink(navigation.id)
        val link = existingLink ?: Link(
            id = UUID.randomUUID(),
            url = "/navigations/${navigation.id}",
            title = "Navigation: ${navigation.name}",
            entityId = navigation.id,
            entityType = "navigation"
        )
        
        if (existingLink == null) {
            linkRepo.insert(link)
        } else {
            linkRepo.update(link)
        }
        
        return navigation
    }
    
    suspend fun delete(id: UUID) {
        // Delete associated link first
        getLink(id)?.let { linkRepo.delete(it.id) }
        // Then delete the navigation
        repo.delete(id)
    }
    
    suspend fun getLink(navigationId: UUID): Link? = 
        linkRepo.getAll().firstOrNull { it.entityId == navigationId }
} 