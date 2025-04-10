package com.hacksync.general.entities

import java.util.*

data class Task(override val id: UUID,
                val number: String,
                val name: String,
                val description: String,
                val priority: Priority
) : IEntity