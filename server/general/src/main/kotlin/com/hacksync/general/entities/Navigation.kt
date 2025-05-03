package com.hacksync.general.entities

import java.time.LocalDate
import java.util.UUID

data class Navigation(
    val id: UUID,
    val description: String?,
    val dateOfRegister: LocalDate?,
    val dateOfStart: LocalDate?,
    val dateOfEnd: LocalDate?,
    val extraDestfine: String?,
    val linkId: UUID?,
    val name: String?
) 