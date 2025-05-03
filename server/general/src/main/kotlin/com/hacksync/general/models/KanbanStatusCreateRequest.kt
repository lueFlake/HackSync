package com.hacksync.general.models

import com.hacksync.general.entities.KanbanStatus
import java.util.UUID

data class KanbanStatusCreateRequest(
    val name: String,
    val color: String,
    val nextId: UUID? = null
) 