package com.hacksync.general.routing

import com.hacksync.general.entities.ChatMessage
import com.hacksync.general.entities.MediaContent
import com.hacksync.general.entities.MessageType
import com.hacksync.general.plugins.configureDependencies
import com.hacksync.general.plugins.serialization.configureSerialization
import com.hacksync.general.repositories.interfaces.MessageRepository
import com.hacksync.general.services.ChatService
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import kotlin.test.*

fun Application.configureTestChatApp() {
    configureDependencies()
    configureSerialization()
    routing {
        addChatRoutes()
    }
}

class InMemoryMessageRepository : MessageRepository {
    private val messages = mutableListOf<ChatMessage>()

    override fun getAll(): List<ChatMessage> = messages

    override fun save(message: ChatMessage) {
        messages.add(message)
    }
}

class ChatControllerTest {
    @Test
    fun `test get message history`() = testApplication {
        // Arrange
        val mockRepo = InMemoryMessageRepository()
        val message = ChatMessage(
            sender = "test_user",
            content = Json.encodeToJsonElement(
                MediaContent(
                    url = "https://example.com/image.jpg",
                    mimeType = "image/jpeg",
                    fileName = "image.jpg",
                    fileSize = 102400,
                    thumbnailUrl = "https://example.com/thumb.jpg"
                )
            ),
            timestamp = 1620000000000,
            type = MessageType.IMAGE
        )
        mockRepo.save(message)

        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(mockRepo) }
                        single<MessageRepository> { mockRepo }
                    }
                )
            }
            configureTestChatApp()
        }

        // Act
        val response = client.get("/chat/history")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val history = Json.decodeFromString<List<ChatMessage>>(response.bodyAsText())
        assertEquals(1, history.size)
        assertEquals(message.sender, history[0].sender)
        assertEquals(message.content, history[0].content)
        assertEquals(message.type, history[0].type)
    }

    @Test
    fun `test websocket connection and message exchange`() = testApplication {
        // Arrange
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val message = ChatMessage(
            sender = "TestUser",
            content = JsonPrimitive("Hello"),
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis()
        )

        client.webSocket("/chat") {
            // Send message
            send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message)))

            // Receive echo
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val receivedMessage = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

            // Assert
            assertEquals(message.sender, receivedMessage.sender)
            assertEquals(message.content, receivedMessage.content)
            assertEquals(message.type, receivedMessage.type)
        }
    }

    @Test
    fun `test multiple clients`() = testApplication {
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val message = ChatMessage(
            sender = "User1",
            content = JsonPrimitive("Hello from User1"),
            type = MessageType.TEXT,
            timestamp = System.currentTimeMillis()
        )

        val client1 = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }
        val client2 = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }

        runBlocking {
            try {
                withTimeout(10000) {
                    val job1 = launch {
                        try {
                            client1.webSocket("/chat") {
                                println("Client1 connected")
                                send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message)))
                                println("Client1 sent message")

                                val response = withTimeout(2000) { incoming.receive() }
                                println("Client1 received response")
                                val received = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

                                assertEquals(message.sender, received.sender)
                                assertEquals(message.content, received.content)
                                assertEquals(message.type, received.type)

                                close()
                            }
                        } catch (e: Exception) {
                            println("Client1 error: ${e.message}")
                            throw e
                        }
                    }

                    val job2 = launch {
                        try {
                            client2.webSocket("/chat") {
                                println("Client2 connected")
                                val response = withTimeout(2000) { incoming.receive() }
                                println("Client2 received message")
                                val received = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

                                assertEquals(message.sender, received.sender)
                                assertEquals(message.content, received.content)
                                assertEquals(message.type, received.type)

                                close()
                            }
                        } catch (e: Exception) {
                            println("Client2 error: ${e.message}")
                            throw e
                        }
                    }

                    joinAll(job1, job2)
                }
            } catch (e: Exception) {
                println("Test error: ${e.message}")
                throw e
            } finally {
                try {
                    client1.close()
                    client2.close()
                } catch (e: Exception) {
                    println("Error closing clients: ${e.message}")
                }
            }
        }
    }

    @Test
    fun `test invalid message format`() = testApplication {
        // Arrange
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val client = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }

        // Act
        client.webSocket("/chat") {
            send(Frame.Text("This is not a valid JSON message"))

            // Assert
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val errorMessage = (response as Frame.Text).readText()
            assertTrue(errorMessage.startsWith("Error:"))
        }
    }

    @Test
    fun `test image message exchange`() = testApplication {
        // Arrange
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val client = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }

        val imageContent = MediaContent(
            url = "https://example.com/image.jpg",
            mimeType = "image/jpeg",
            fileName = "test_image.jpg",
            fileSize = 102400,
            thumbnailUrl = "https://example.com/thumb.jpg"
        )

        val message = ChatMessage(
            sender = "TestUser",
            content = Json.encodeToJsonElement(imageContent),
            type = MessageType.IMAGE,
            timestamp = System.currentTimeMillis()
        )

        client.webSocket("/chat") {
            // Send image message
            send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message)))

            // Receive echo
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val receivedMessage = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

            // Assert
            assertEquals(message.sender, receivedMessage.sender)
            assertEquals(message.type, receivedMessage.type)
            assertEquals(message.content, receivedMessage.content)
        }
    }

    @Test
    fun `test video message exchange`() = testApplication {
        // Arrange
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val client = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }

        val videoContent = MediaContent(
            url = "https://example.com/video.mp4",
            mimeType = "video/mp4",
            fileName = "test_video.mp4",
            fileSize = 1024000,
            thumbnailUrl = "https://example.com/video_thumb.jpg"
        )

        val message = ChatMessage(
            sender = "TestUser",
            content = Json.encodeToJsonElement(videoContent),
            type = MessageType.VIDEO,
            timestamp = System.currentTimeMillis()
        )

        client.webSocket("/chat") {
            // Send video message
            send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message)))

            // Receive echo
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val receivedMessage = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

            // Assert
            assertEquals(message.sender, receivedMessage.sender)
            assertEquals(message.type, receivedMessage.type)
            assertEquals(message.content, receivedMessage.content)
        }
    }

    @Test
    fun `test file message exchange`() = testApplication {
        // Arrange
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val client = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }

        val fileContent = MediaContent(
            url = "https://example.com/document.pdf",
            mimeType = "application/pdf",
            fileName = "test_document.pdf",
            fileSize = 512000,
            thumbnailUrl = null
        )

        val message = ChatMessage(
            sender = "TestUser",
            content = Json.encodeToJsonElement(fileContent),
            type = MessageType.FILE,
            timestamp = System.currentTimeMillis()
        )

        client.webSocket("/chat") {
            // Send file message
            send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message)))

            // Receive echo
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val receivedMessage = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

            // Assert
            assertEquals(message.sender, receivedMessage.sender)
            assertEquals(message.type, receivedMessage.type)
            assertEquals(message.content, receivedMessage.content)
        }
    }

    @Test
    fun `test multiple clients with media messages`() = testApplication {
        application {
            install(Koin) {
                modules(
                    module {
                        single { ChatService(get()) }
                        single<MessageRepository> { InMemoryMessageRepository() }
                    }
                )
            }
            configureTestChatApp()
        }

        val imageContent = MediaContent(
            url = "https://example.com/image.jpg",
            mimeType = "image/jpeg",
            fileName = "test_image.jpg",
            fileSize = 102400,
            thumbnailUrl = "https://example.com/thumb.jpg"
        )

        val message = ChatMessage(
            sender = "User1",
            content = Json.encodeToJsonElement(imageContent),
            type = MessageType.IMAGE,
            timestamp = System.currentTimeMillis()
        )

        val client1 = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }
        val client2 = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }

        try {
            withTimeout(5000) {
                val job1 = launch {
                    client1.webSocket("/chat") {
                        send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message)))

                        val response = withTimeout(1000) { incoming.receive() }
                        val received = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

                        assertEquals(message.sender, received.sender)
                        assertEquals(message.content, received.content)
                        assertEquals(message.type, received.type)

                        close()
                    }
                }

                val job2 = launch {
                    client2.webSocket("/chat") {
                        val response = withTimeout(1000) { incoming.receive() }
                        println("Client2 received image message")
                        val received = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())

                        assertEquals(message.sender, received.sender)
                        assertEquals(message.content, received.content)
                        assertEquals(message.type, received.type)

                        close()
                    }
                }

                joinAll(job1, job2)
            }
        } finally {
            client1.close()
            client2.close()
        }
    }
}