package com.hacksync.general.commands.team

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReadTeamCommand(
    @Contextual
    val id: UUID
) 