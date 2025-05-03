package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Hackathon
import java.util.UUID

interface HackathonRepository {
    suspend fun getAll(): List<Hackathon>
    suspend fun getById(id: UUID): Hackathon?
    suspend fun insert(hackathon: Hackathon): Hackathon
    suspend fun update(hackathon: Hackathon)
    suspend fun delete(id: UUID)
} 