package com.hacksync.general.entities

import java.util.UUID

data class UserTeam(
    val userId: UUID,
    val teamId: UUID,
    val role: String = "PARTICIPANT" // Default role for joining users
) 