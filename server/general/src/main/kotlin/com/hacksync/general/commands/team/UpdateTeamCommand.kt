package com.hacksync.general.commands.team

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UpdateTeamCommand(
    @Contextual
    val id: UUID,
    val name: String? = null
) 