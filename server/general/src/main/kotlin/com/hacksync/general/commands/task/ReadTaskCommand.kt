package com.hacksync.general.commands.task

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReadTaskCommand(
    @Contextual
    val id: UUID
)