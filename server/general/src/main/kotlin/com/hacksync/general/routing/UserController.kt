package com.hacksync.general.routing

import com.hacksync.general.commands.auth.ChangePasswordCommand
import com.hacksync.general.commands.user.CreateUserCommand
import com.hacksync.general.commands.user.DeleteUserCommand
import com.hacksync.general.commands.user.ReadUserCommand
import com.hacksync.general.commands.user.UpdateUserCommand
import com.hacksync.general.docs.UserDocs
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.scope
import com.hacksync.general.services.UserService
import com.hacksync.general.utils.jsonBody
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.content.*
import io.ktor.server.plugins.*
import java.io.File
import java.util.*

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

val getUserLink: RouteConfig.() -> Unit = {
    operationId = "getUserLink"
    summary = "Get user's link"
    description = "Retrieves the link associated with a user"
    tags = listOf("Users")

    response {
        HttpStatusCode.OK to {
            description = "User's link retrieved successfully"
        }
        HttpStatusCode.BadRequest to {
            description = "Invalid ID format"
        }
        HttpStatusCode.NotFound to {
            description = "User or link not found"
        }
    }
}
fun Route.addUserRoutes() {
    route("/users") {
        post(UserDocs.postUser) {
            val user = call.receive<CreateUserCommand>()
            val userId = call.scope.get<UserService>().create(user)
            call.respond(HttpStatusCode.Created, userId)
        }

        get("/{id}", UserDocs.getUserById) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val user = call.scope.get<UserService>().read(ReadUserCommand(id))
            call.respond(HttpStatusCode.OK, user.toDto())
        }

        get("/all", UserDocs.getAllUsers) {
            val users = call.scope.get<UserService>().getAll()
            call.respond(users.map { it.toDto() })
        }

        get("/{id}/link", getUserLink) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val link = call.scope.get<UserService>().getLink(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Link not found")
            call.respond(HttpStatusCode.OK, link.toDto())
        }

        put("/{id}", UserDocs.putUser) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val command = call.receive<UpdateUserCommand>()
            call.scope.get<UserService>().update(command)
            call.respond(HttpStatusCode.OK)
        }

        post("/{id}/change-password", UserDocs.changePassword) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@post
            }
            val command = call.receive<ChangePasswordCommand>()
            call.scope.get<UserService>().changePassword(command)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}", UserDocs.deleteUser) {
            val id = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            call.scope.get<UserService>().delete(DeleteUserCommand(id))
            call.respond(HttpStatusCode.OK)
        }

        post("/{id}/avatar") {
            val userId = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                return@post
            }

            val maxFileSize = 5 * 1024 * 1024 // 5 MB
            var fileUrl: String? = null
            // Get user and check if exists
            val user = call.scope.get<UserService>().read(ReadUserCommand(userId))

            try {
                val multipart = call.receiveMultipart()

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        
                        // Check file type
                        val fileExtension = part.originalFileName?.substringAfterLast('.', "")?.lowercase()
                        val allowedExtensions = setOf("jpg", "jpeg", "png", "webp")
                        
                        if (fileExtension !in allowedExtensions) {
                            throw BadRequestException("Only JPG, PNG, or WEBP images are allowed")
                        }

                        // Check file size
                        val contentLength = part.headers["Content-Length"]?.toLongOrNull()
                        if (contentLength != null && contentLength > maxFileSize) {
                            throw BadRequestException("File size exceeds 5MB limit")
                        }

                        // Delete old avatar if exists
                        user.avatarUrl?.let { oldUrl ->
                            try {
                                val oldFile = File(oldUrl.removePrefix("/"))
                                if (oldFile.exists()) {
                                    oldFile.delete()
                                    // logger.info("Deleted old avatar: ${oldFile.path}")
                                }
                            } catch (e: Exception) {
                                // logger.error("Failed to delete old avatar", e)
                                // Continue execution - file might already be deleted
                            }
                        }

                        // Save new avatar
                        val fileName = "avatar_${userId}.$fileExtension"
                        val uploadDir = File("uploads/avatars").apply { mkdirs() }
                        val file = File(uploadDir, fileName)

                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }

                        fileUrl = "/uploads/avatars/$fileName"
                    }
                    part.dispose()
                }

                if (fileUrl != null) {
                    call.scope.get<UserService>().update(UpdateUserCommand(
                        id = userId,
                        avatarUrl = fileUrl
                    ))
                    call.respond(mapOf("url" to fileUrl))
                } else {
                    throw BadRequestException("Failed to process uploaded file")
                }
            } catch (e: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                 println("Avatar upload failed " + e.toString())
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to upload avatar"))
            }
        }

        get("/{id}/avatar") {
            val userId = UUID.fromString(call.parameters["id"]) ?: run {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user ID"))
                return@get
            }
            val user = call.scope.get<UserService>().read(ReadUserCommand(userId))

            try {
                // Check if user has avatar
                val avatarUrl = user.avatarUrl ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "User has no avatar")
                )

                // Get avatar file
                val avatarFile = File(avatarUrl.removePrefix("/"))
                if (!avatarFile.exists()) {
                    return@get call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "Avatar file not found")
                    )
                }

                // Set cache headers
                call.response.header(
                    HttpHeaders.CacheControl,
                    "public, max-age=31536000" // Cache for 1 year
                )

                // Set content type based on file extension
                val contentType = when (avatarFile.extension.lowercase()) {
                    "jpg", "jpeg" -> ContentType.Image.JPEG
                    "png" -> ContentType.Image.PNG
                    "webp" -> ContentType("image", "webp")
                    else -> ContentType.Application.OctetStream
                }
                call.response.header(HttpHeaders.ContentType, contentType.toString())

                // Send file
                call.respondFile(avatarFile)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to retrieve avatar")
                )
            }
        }
    }
}