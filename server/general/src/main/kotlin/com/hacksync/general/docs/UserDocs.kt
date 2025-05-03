package com.hacksync.general.docs

import com.hacksync.general.commands.*
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.util.*

object UserDocs {
    val postUser: RouteConfig.() -> Unit = {
        operationId = "postUser"
        summary = "Create a new user"
        description = "Creates a new user and returns their ID"
        tags = listOf("Users")

        request {
            jsonBody<CreateUserCommand> {
                example("Create User") {
                    value = CreateUserCommand(
                        email = "user@example.com",
                        name = "John Doe",
                        password = "password123",
                        role = "PARTICIPANT",
                        avatarUrl = "url"
                    )
                    description = "Create a new user with email, name and password"
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "User created successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
        }
    }

    val getUserById: RouteConfig.() -> Unit = {
        operationId = "getUserById"
        summary = "Get user by ID"
        description = "Retrieves a user by their UUID"
        tags = listOf("Users")

        response {
            HttpStatusCode.OK to {
                description = "User found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
            HttpStatusCode.NotFound to {
                description = "User not found"
            }
        }
    }

    val getAllUsers: RouteConfig.() -> Unit = {
        operationId = "getAllUsers"
        summary = "Get all users"
        description = "Retrieves a list of all users"
        tags = listOf("Users")

        response {
            HttpStatusCode.OK to {
                description = "List of users"
            }
        }
    }

    val putUser: RouteConfig.() -> Unit = {
        operationId = "putUser"
        summary = "Update user"
        description = "Updates an existing user's information"
        tags = listOf("Users")

        request {
            jsonBody<UpdateUserCommand> {
                example("Update User") {
                    value = UpdateUserCommand(
                        id = UUID.randomUUID(),
                        email = "updated@example.com",
                        name = "Updated Name"
                    )
                    description = "Update user's email and name"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "User updated successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "User not found"
            }
        }
    }

    val changePassword: RouteConfig.() -> Unit = {
        operationId = "changePassword"
        summary = "Change user password"
        description = "Updates a user's password"
        tags = listOf("Users")

        request {
            jsonBody<ChangePasswordCommand> {
                example("Change Password") {
                    value = ChangePasswordCommand(
                        id = UUID.randomUUID(),
                        oldPassword = "oldpass123",
                        newPassword = "newpass123"
                    )
                    description = "Change user's password"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Password changed successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
            HttpStatusCode.NotFound to {
                description = "User not found"
            }
        }
    }

    val deleteUser: RouteConfig.() -> Unit = {
        operationId = "deleteUser"
        summary = "Delete user"
        description = "Deletes a user by their UUID"
        tags = listOf("Users")

        response {
            HttpStatusCode.OK to {
                description = "User deleted successfully"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
            HttpStatusCode.NotFound to {
                description = "User not found"
            }
        }
    }
} 