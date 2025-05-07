package com.hacksync.general.docs

import com.hacksync.general.commands.task.CreateTaskCommand
import com.hacksync.general.commands.task.UpdateTaskCommand
import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.entities.Priority
import com.hacksync.general.entities.Task
import com.hacksync.general.utils.jsonBody
import com.hacksync.general.utils.standardListQueryParameters
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.*
import java.time.Instant
import java.util.UUID

object TaskDocs {
    val getAllTasks: RouteConfig.() -> Unit = {
        operationId = "getAllTasks"
        summary = "Get all tasks"
        description = "Retrieves a list of all tasks in the system"
        tags = listOf("Tasks")

        request {
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "List of all tasks"
                jsonBody<List<Task>> {
                    example("All Tasks") {
                        value = listOf(
                            Task(
                                id = UUID.randomUUID(),
                                number = "TASK-1",
                                name = "Implement authentication",
                                description = "Add JWT authentication to the backend",
                                priority = Priority.HIGH,
                                linkId = UUID.randomUUID(),
                                userId = UUID.randomUUID(),
                                status = KanbanStatus(
                                    id = UUID.randomUUID(),
                                    nextId = null,
                                    name = "In Progress",
                                    color = "#FFA500",
                                    createdAt = Instant.now(),
                                    updatedAt = Instant.now()
                                ),
                                dueDate = Instant.now().plusSeconds(86400),
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of all tasks in the system"
                    }
                }
            }
        }
    }

    val getTaskById: RouteConfig.() -> Unit = {
        operationId = "getTaskById"
        summary = "Get task by ID"
        description = "Retrieves a specific task by its ID"
        tags = listOf("Tasks")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the task to retrieve"
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Task retrieved successfully"
                jsonBody<Task> {
                    example("Single Task") {
                        value = Task(
                            id = UUID.randomUUID(),
                            number = "TASK-1",
                            name = "Implement authentication",
                            description = "Add JWT authentication to the backend",
                            priority = Priority.HIGH,
                            linkId = UUID.randomUUID(),
                            userId = UUID.randomUUID(),
                            status = KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = null,
                                name = "In Progress",
                                color = "#FFA500",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            dueDate = Instant.now().plusSeconds(86400),
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a single task"
                    }
                }
            }
            HttpStatusCode.NotFound to {
                description = "Task not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }

    val getTasksByUserId: RouteConfig.() -> Unit = {
        operationId = "getTasksByUserId"
        summary = "Get tasks by user ID"
        description = "Retrieves all tasks assigned to a specific user"
        tags = listOf("Tasks")

        request {
            pathParameter<UUID>("userId") {
                description = "The ID of the user whose tasks to retrieve"
            }
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "List of tasks assigned to the user"
                jsonBody<List<Task>> {
                    example("User Tasks") {
                        value = listOf(
                            Task(
                                id = UUID.randomUUID(),
                                number = "TASK-1",
                                name = "Implement authentication",
                                description = "Add JWT authentication to the backend",
                                priority = Priority.HIGH,
                                linkId = UUID.randomUUID(),
                                userId = UUID.randomUUID(),
                                status = KanbanStatus(
                                    id = UUID.randomUUID(),
                                    nextId = null,
                                    name = "In Progress",
                                    color = "#FFA500",
                                    createdAt = Instant.now(),
                                    updatedAt = Instant.now()
                                ),
                                dueDate = Instant.now().plusSeconds(86400),
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of tasks assigned to a user"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid user ID format"
            }
        }
    }

    val getTasksByStatusId: RouteConfig.() -> Unit = {
        operationId = "getTasksByStatusId"
        summary = "Get tasks by status ID"
        description = "Retrieves all tasks with a specific status"
        tags = listOf("Tasks")

        request {
            pathParameter<UUID>("statusId") {
                description = "The ID of the status to filter tasks by"
            }
            standardListQueryParameters()
        }

        response {
            HttpStatusCode.OK to {
                description = "List of tasks with the specified status"
                jsonBody<List<Task>> {
                    example("Status Tasks") {
                        value = listOf(
                            Task(
                                id = UUID.randomUUID(),
                                number = "TASK-1",
                                name = "Implement authentication",
                                description = "Add JWT authentication to the backend",
                                priority = Priority.HIGH,
                                linkId = UUID.randomUUID(),
                                userId = UUID.randomUUID(),
                                status = KanbanStatus(
                                    id = UUID.randomUUID(),
                                    nextId = null,
                                    name = "In Progress",
                                    color = "#FFA500",
                                    createdAt = Instant.now(),
                                    updatedAt = Instant.now()
                                ),
                                dueDate = Instant.now().plusSeconds(86400),
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            )
                        )
                        description = "Example of tasks with a specific status"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid status ID format"
            }
        }
    }

    val createTask: RouteConfig.() -> Unit = {
        operationId = "createTask"
        summary = "Create new task"
        description = "Creates a new task with default Backlog status"
        tags = listOf("Tasks")

        request {
            jsonBody<CreateTaskCommand> {
                example("Create Task") {
                    value = CreateTaskCommand(
                        number = "TASK-1",
                        name = "Implement authentication",
                        description = "Add JWT authentication to the backend",
                        priority = Priority.HIGH,
                        linkId = UUID.randomUUID(),
                        userId = UUID.randomUUID(),
                        dueDate = Instant.now().plusSeconds(86400)
                    )
                    description = "Create a new task with all required fields. Status will be set to Backlog by default."
                }
            }
        }

        response {
            HttpStatusCode.Created to {
                description = "Task created successfully"
                jsonBody<Task> {
                    example("Created Task") {
                        value = Task(
                            id = UUID.randomUUID(),
                            number = "TASK-1",
                            name = "Implement authentication",
                            description = "Add JWT authentication to the backend",
                            priority = Priority.HIGH,
                            linkId = UUID.randomUUID(),
                            userId = UUID.randomUUID(),
                            status = KanbanStatus(
                                id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
                                nextId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
                                name = "Backlog",
                                color = "#808080",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            dueDate = Instant.now().plusSeconds(86400),
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of a created task with default Backlog status"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data"
            }
        }
    }

    val updateTask: RouteConfig.() -> Unit = {
        operationId = "updateTask"
        summary = "Update task"
        description = "Updates an existing task"
        tags = listOf("Tasks")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the task to update"
            }
            jsonBody<UpdateTaskCommand> {
                example("Update Task") {
                    value = UpdateTaskCommand(
                        id = UUID.randomUUID(),
                        number = "TASK-1",
                        name = "Updated Task Name",
                        description = "Updated task description",
                        priority = Priority.MEDIUM,
                        linkId = UUID.randomUUID(),
                        userId = UUID.randomUUID(),
                        statusId = UUID.randomUUID(),
                        dueDate = Instant.now().plusSeconds(172800)
                    )
                    description = "Update an existing task with optional fields"
                }
            }
        }

        response {
            HttpStatusCode.OK to {
                description = "Task updated successfully"
                jsonBody<Task> {
                    example("Updated Task") {
                        value = Task(
                            id = UUID.randomUUID(),
                            number = "TASK-1",
                            name = "Updated Task Name",
                            description = "Updated task description",
                            priority = Priority.MEDIUM,
                            linkId = UUID.randomUUID(),
                            userId = UUID.randomUUID(),
                            status = KanbanStatus(
                                id = UUID.randomUUID(),
                                nextId = null,
                                name = "In Progress",
                                color = "#FFA500",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now()
                            ),
                            dueDate = Instant.now().plusSeconds(172800),
                            createdAt = Instant.now(),
                            updatedAt = Instant.now()
                        )
                        description = "Example of an updated task"
                    }
                }
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid input data or ID mismatch"
            }
            HttpStatusCode.NotFound to {
                description = "Task not found"
            }
        }
    }

    val deleteTask: RouteConfig.() -> Unit = {
        operationId = "deleteTask"
        summary = "Delete task"
        description = "Deletes a task by its ID"
        tags = listOf("Tasks")

        request {
            pathParameter<UUID>("id") {
                description = "The ID of the task to delete"
            }
        }

        response {
            HttpStatusCode.NoContent to {
                description = "Task deleted successfully"
            }
            HttpStatusCode.NotFound to {
                description = "Task not found"
            }
            HttpStatusCode.BadRequest to {
                description = "Invalid ID format"
            }
        }
    }
} 