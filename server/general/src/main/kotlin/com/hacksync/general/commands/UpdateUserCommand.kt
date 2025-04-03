package com.hacksync.general.commands

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateUserCommand(
    @Contextual
    val id: UUID,
    val email: String? = null,
    val name: String? = null,
    val avatarUrl: String? = null
)