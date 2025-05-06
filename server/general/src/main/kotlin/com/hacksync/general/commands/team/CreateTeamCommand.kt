package com.hacksync.general.commands.team

import com.hacksync.general.entities.Team
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class CreateTeamCommand(
    val name: String
) {
    fun execute(): Team {
        return Team(
            id = UUID.randomUUID(),
            linkId = null,
            name = this.name,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
} 