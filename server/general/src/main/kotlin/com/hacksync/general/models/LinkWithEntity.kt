package com.hacksync.general.models

import com.hacksync.general.dto.BaseDto
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LinkWithEntity(
    @Contextual
    val id: UUID,
    val url: String,
    val title: String,
    @Contextual
    val entityId: UUID,
    val entityType: String,
    @Contextual
    val entityInfo: BaseDto?
) 