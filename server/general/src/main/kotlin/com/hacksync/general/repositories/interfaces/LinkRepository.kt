package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Link
import java.util.UUID

interface LinkRepository {
    fun getAll(): List<Link>
    fun getById(id: UUID): Link?
    fun getByTitle(title: String): List<Link>
    fun insert(link: Link)
    fun update(link: Link)
    fun delete(id: UUID)
} 