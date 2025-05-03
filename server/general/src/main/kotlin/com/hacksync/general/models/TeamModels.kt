package com.hacksync.general.models

import com.hacksync.general.entities.Team
import com.hacksync.general.entities.Link
import java.util.UUID

data class TeamCreateRequest(
    val team: Team
)

data class TeamResponse(
    val team: Team,
    val link: Link?
) 