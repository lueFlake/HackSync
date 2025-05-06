package com.hacksync.general.plugins.serialization

import com.hacksync.general.dto.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import com.hacksync.general.plugins.serialization.InstantSerializer
import com.hacksync.general.plugins.serialization.UuidSerializer
import kotlinx.serialization.modules.polymorphic
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        json(Json {
                serializersModule = SerializersModule {polymorphic(BaseDto::class) {
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
        })
    }
    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
