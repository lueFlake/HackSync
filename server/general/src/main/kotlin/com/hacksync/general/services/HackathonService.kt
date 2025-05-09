package com.hacksync.general.services

import com.hacksync.general.commands.hackathon.CreateHackathonCommand
import com.hacksync.general.commands.hackathon.DeleteHackathonCommand
import com.hacksync.general.commands.hackathon.ReadHackathonCommand
import com.hacksync.general.commands.hackathon.UpdateHackathonCommand
import com.hacksync.general.entities.Hackathon
import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.JdbiLinkRepository
import com.hacksync.general.repositories.interfaces.HackathonRepository
import com.hacksync.general.repositories.interfaces.LinkRepository
import java.util.*

class HackathonService(
    private val hackathonRepository: HackathonRepository,
    private val linkRepository: LinkRepository
) {
    suspend fun read(command: ReadHackathonCommand): Hackathon {
        return hackathonRepository.getById(command.id)!!
    }

    suspend fun create(command: CreateHackathonCommand): UUID {
        val hackathon = command.execute()
        hackathonRepository.insert(hackathon)
        
        // Generate and create a link for the hackathon
        val link = Link(
            id = UUID.randomUUID(),
            url = "/hackathons/${hackathon.id}",
            title = "Hackathon: ${hackathon.name}",
            entityId = hackathon.id,
            entityType = "hackathon"
        )
        linkRepository.insert(link)
        
        return hackathon.id
    }

    suspend fun update(command: UpdateHackathonCommand) {
        val existingHackathon = hackathonRepository.getById(command.id)!!
        val updatedHackathon = existingHackathon.copy(
            name = command.name ?: existingHackathon.name,
            description = command.description ?: existingHackathon.description,
            dateOfRegister = command.toInstant(command.dateOfRegister) ?: existingHackathon.dateOfRegister,
            dateOfStart = command.toInstant(command.dateOfStart) ?: existingHackathon.dateOfStart,
            dateOfEnd = command.toInstant(command.dateOfEnd) ?: existingHackathon.dateOfEnd
        )
        hackathonRepository.update(updatedHackathon)
        
        // Update the existing link or create a new one if it doesn't exist
        getLink(command.id)?.let { linkRepository.delete(it.id) }
        
        val link = Link(
            id = UUID.randomUUID(),
            url = "/hackathons/${command.id}",
            title = "Hackathon: ${updatedHackathon.name}",
            entityId = command.id,
            entityType = "hackathon"
        )
        linkRepository.insert(link)
    }

    suspend fun delete(command: DeleteHackathonCommand) {
        // Delete associated link first
        getLink(command.id)?.let { linkRepository.delete(it.id) }
        // Then delete the hackathon
        hackathonRepository.delete(command.id)
    }

    suspend fun getAll(): List<Hackathon> = hackathonRepository.getAll()

    private suspend fun getLink(hackathonId: UUID): Link? =
        linkRepository.getAll().firstOrNull { it.entityId == hackathonId }
} 