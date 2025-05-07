package com.hacksync.general.models

import com.hacksync.general.entities.User
import com.hacksync.general.entities.Link
import kotlinx.serialization.Serializable
import java.util.UUID

data class UserCreateRequest(
    val user: User
)

/*data class UserResponse(
    val user: User,
    val link: Link?
)*/

@Serializable
data class UserResponse(
    val userId : String,
    val email : String,
    val name : String
)