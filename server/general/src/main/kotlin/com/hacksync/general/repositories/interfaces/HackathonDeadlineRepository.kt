package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.Deadline
import java.util.UUID

interface HackathonDeadlineRepository {
    fun getDeadlinesForHackathon(hackathonId: UUID): List<Deadline>
    fun addDeadlineToHackathon(hackathonId: UUID, deadlineId: UUID)
    fun removeDeadlineFromHackathon(hackathonId: UUID, deadlineId: UUID)
    fun removeAllDeadlinesFromHackathon(hackathonId: UUID)
} 