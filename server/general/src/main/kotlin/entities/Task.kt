package entities

import java.util.*

data class Task(val id: UUID,
                val number: String,
                val name: String,
                val description: String,
                val priority: Priority)