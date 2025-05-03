package com.hacksync.general.models

import com.hacksync.general.entities.Navigation
import com.hacksync.general.entities.Link
import java.util.UUID

data class NavigationCreateRequest(
    val navigation: Navigation
)

data class NavigationResponse(
    val navigation: Navigation,
    val link: Link?
) 