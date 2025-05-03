package com.hacksync.general.services

import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiLinkRepository
import java.util.UUID

class LinkService(private val repo: JdbiLinkRepository) {
    suspend fun getAll(): List<Link> = repo.getAll()
    suspend fun getById(id: UUID): Link? = repo.getById(id)
    suspend fun create(link: Link) = repo.insert(link)
    suspend fun update(link: Link) = repo.update(link)
    suspend fun delete(id: UUID) = repo.delete(id)
} 