package com.hacksync.general.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LinkDto(
    @Contextual
    val id: UUID,
    val url: String,
    val title: String,
    @Contextual
    val entityId: UUID,
    val entityType: String
) : BaseDto