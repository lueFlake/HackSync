package com.hacksync.general.services

import com.hacksync.general.entities.Link
import com.hacksync.general.models.LinkWithEntity
import com.hacksync.general.repositories.JdbiDeadlineRepository
import com.hacksync.general.repositories.JdbiLinkRepository
import com.hacksync.general.repositories.JdbiTaskRepository
import com.hacksync.general.repositories.JdbiTeamRepository
import com.hacksync.general.repositories.interfaces.HackathonRepository
import kotlinx.serialization.json.Json
import com.hacksync.general.dto.*
import com.hacksync.general.plugins.serialization.InstantSerializer
import com.hacksync.general.plugins.serialization.UuidSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.util.UUID
import java.time.Instant

val BaseDtoSerializersModule = SerializersModule {
    polymorphic(BaseDto::class) {
        subclass(TaskDto::class, TaskDto.serializer())
        subclass(HackathonDto::class, HackathonDto.serializer())
        subclass(DeadlineDto::class, DeadlineDto.serializer())
        subclass(TeamDto::class, TeamDto.serializer())
        subclass(UserDto::class, UserDto.serializer())
        subclass(LinkDto::class, LinkDto.serializer())
    }
    contextual(Instant::class, InstantSerializer)
    contextual(UUID::class, UuidSerializer)
}

val PolymorphicJson = Json {
    serializersModule = BaseDtoSerializersModule
    classDiscriminator = "type"
}

class LinkService(
    private val repo: JdbiLinkRepository,
    private val hackathonRepo: HackathonRepository,
    private val teamRepo: JdbiTeamRepository,
    private val deadlineRepo: JdbiDeadlineRepository,
    private val taskRepo: JdbiTaskRepository
) {
    suspend fun getAll(): List<Link> = repo.getAll()
    suspend fun getById(id: UUID): Link? = repo.getById(id)
    suspend fun getByTitle(title: String): List<LinkWithEntity> {
        val links = repo.getByTitle(title)
        return links.map { link ->
            val entityDto: BaseDto? = when (link.entityType.uppercase()) {
                "HACKATHON" -> hackathonRepo.getById(link.entityId)?.toDto()
                "TEAM" -> teamRepo.getById(link.entityId)?.toDto()
                "DEADLINE" -> deadlineRepo.getById(link.entityId)?.toDto()
                "TASK" -> taskRepo.getById(link.entityId)?.toDto()
                else -> null
            }
            LinkWithEntity(
                id = link.id,
                url = link.url,
                title = link.title,
                entityId = link.entityId,
                entityType = link.entityType,
                entityInfo = entityDto // entityDto?.let { PolymorphicJson.encodeToString(PolymorphicSerializer(BaseDto::class), it) } ?: "null"
            )
        }
    }
    suspend fun create(link: Link) = repo.insert(link)
    suspend fun update(link: Link) = repo.update(link)
    suspend fun delete(id: UUID) = repo.delete(id)
} 