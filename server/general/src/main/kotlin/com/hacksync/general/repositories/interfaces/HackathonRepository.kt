package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Hackathon
import java.util.UUID

interface HackathonRepository {
    fun getAll(): List<Hackathon>
    fun getById(id: UUID): Hackathon?
    fun insert(hackathon: Hackathon)
    fun update(hackathon: Hackathon)
    fun delete(id: UUID)
}