package com.hacksync.general.entities

import java.time.LocalDate
import java.util.UUID

data class Deadline(
    val id: UUID,
    val date: LocalDate?,
    val linkId: UUID?,
    val name: String?,
    val type: String?
) 