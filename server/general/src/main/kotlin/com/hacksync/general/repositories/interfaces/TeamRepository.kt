package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Team
import java.util.UUID

interface TeamRepository {
    fun getAll(): List<Team>
    fun getById(id: UUID): Team?
    fun insert(team: Team)
    fun update(team: Team)
    fun delete(id: UUID)
} 