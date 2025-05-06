package com.hacksync.general.commands.team

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GetUserTeamsCommand(
    @Contextual
    val userId: UUID
) 