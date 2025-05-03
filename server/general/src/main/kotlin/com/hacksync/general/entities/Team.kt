package com.hacksync.general.entities

import java.util.UUID

data class Team(
    val id: UUID,
    val linkId: UUID?,
    val name: String?
)