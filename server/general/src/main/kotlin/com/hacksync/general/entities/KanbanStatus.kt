package com.hacksync.general.entities

import java.util.UUID

data class KanbanStatus(
    val id: UUID,
    val nextId: UUID?,
    val name: String,
    val color: String
) 