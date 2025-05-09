package com.hacksync.general.commands.hackathon

import com.hacksync.general.entities.Hackathon
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

@Serializable
data class CreateHackathonCommand(
    val name: String,
    val description: String,
    val dateOfRegister: String,
    val dateOfStart: String,
    val dateOfEnd: String
) {
    fun execute(): Hackathon {
        return Hackathon(
            id = UUID.randomUUID(),
            name = this.name,
            description = this.description,
            dateOfRegister = Instant.parse(this.dateOfRegister),
            dateOfStart = Instant.parse(this.dateOfStart),
            dateOfEnd = Instant.parse(this.dateOfEnd),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}