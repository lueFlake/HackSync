package com.hacksync.general.commands.task

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReadTasksByUserIdCommand(
    @Contextual
    val userId: UUID,
    val page: Int = 1,
    val size: Int = 10
) 