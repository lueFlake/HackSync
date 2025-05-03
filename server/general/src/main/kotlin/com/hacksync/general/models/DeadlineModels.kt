package com.hacksync.general.models

import com.hacksync.general.entities.Deadline
import com.hacksync.general.entities.Link
import java.util.UUID

data class DeadlineCreateRequest(
    val deadline: Deadline
)

data class DeadlineResponse(
    val deadline: Deadline,
    val link: Link?
) 