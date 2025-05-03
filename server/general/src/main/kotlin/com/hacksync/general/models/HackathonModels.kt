package com.hacksync.general.models

import com.hacksync.general.entities.Hackathon
import com.hacksync.general.entities.Link
import java.util.UUID

data class HackathonCreateRequest(
    val hackathon: Hackathon
)

data class HackathonResponse(
    val hackathon: Hackathon,
    val link: Link?
) 