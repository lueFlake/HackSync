package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Hackathon
import java.util.UUID

interface HackathonRepository {
    fun insert(hackathon: Hackathon)
    fun getById(id: UUID): Hackathon?
    fun getAll(): List<Hackathon>
    fun update(hackathon: Hackathon)
    fun delete(id: UUID)
}