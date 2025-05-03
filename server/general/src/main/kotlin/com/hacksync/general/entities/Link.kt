package com.hacksync.general.entities

import java.util.UUID

data class Link(
    override val id: UUID,
    val url: String,
    val title: String,
    val entityId: UUID,
    val entityType: String  // e.g. "user", "team", "deadline", "navigation"
) : IEntity 