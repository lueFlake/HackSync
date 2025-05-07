package com.hacksync.general.routing

import com.hacksync.general.configureInjection
import com.hacksync.general.model.ChatMessage
import com.hacksync.general.model.MediaContent
import com.hacksync.general.model.MessageType
import com.hacksync.general.plugins.configureDependencies
import com.hacksync.general.plugins.serialization.configureSerialization
import com.hacksync.general.repositories.InMemoryMessageRepository
import com.hacksync.general.repositories.JdbiUserRepository
import com.hacksync.general.repositories.interfaces.MessageRepository
import com.hacksync.general.services.ChatService
import com.hacksync.general.utils.withWebSocket
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.test.KoinTest
import io.ktor.server.testing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.mockk.coEvery
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.ext.get
import io.mockk.mockk
import io.mockk.every
import kotlinx.serialization.encodeToString
import org.jdbi.v3.core.Jdbi
import org.koin.core.module.dsl.scopedOf
import org.koin.module.requestScope
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

fun Application.configureTestChatApp() {
    configureDependencies()
    configureSerialization()
    routing {
        addChatRoutes()
    }
}

class ChatControllerTest {
    @Test
    fun `test get message history`() = testApplication {
        // Arrange
        val mockRepo = mockk<MessageRepository>()
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
        every { mockRepo.getAll() } returns listOf(message)
        application {
            install(Koin) {
                modules(
                    module {
                        requestScope { scopedOf(::ChatService) }
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
        val str = response.bodyAsText()
        val history = Json.decodeFromString<List<ChatMessage>>(str)
        assertEquals(1, history.size)
        assertEquals(message.sender, history[0].sender)
        assertEquals(message.content, history[0].content)
        assertEquals(message.type, history[0].type)
    }

    @Test
    fun `test websocket connection and message exchange`() = testApplication {
        application { configureTestChatApp() }
        // Arrange
        val clientMessage = ChatMessage(
            sender = "TestUser",
            content = JsonPrimitive("Hello"),
            type = MessageType.TEXT
        )
        val clientMessageJson = Json.encodeToString(ChatMessage.serializer(), clientMessage)

        // Act & Assert
        withWebSocket("/chat") { incoming, outgoing ->
            // Send message
            outgoing.send(Frame.Text(clientMessageJson))

            // Receive the same message back (broadcast)
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val receivedMessage = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())
            
            assertEquals(clientMessage.sender, receivedMessage.sender)
            assertEquals(clientMessage.content, receivedMessage.content)
            assertEquals(clientMessage.type, receivedMessage.type)
        }
    }

    @Test
    fun `test multiple clients`() = testApplication {
        application { configureTestChatApp() }
        // Arrange
        val message1 = ChatMessage(
            sender = "User1",
            content = JsonPrimitive("Hello from User1"),
            type = MessageType.TEXT
        )
        val message2 = ChatMessage(
            sender = "User2",
            content = JsonPrimitive("Hello from User2"),
            type = MessageType.TEXT
        )

        // Act & Assert
        withWebSocket("/chat") { incoming1, outgoing1 ->
            withWebSocket("/chat") { incoming2, outgoing2 ->
                // Send message from first client
                outgoing1.send(Frame.Text(Json.encodeToString(ChatMessage.serializer(), message1)))
                
                // Both clients should receive the message
                val response1 = incoming1.receive()
                val response2 = incoming2.receive()
                
                assertTrue(response1 is Frame.Text)
                assertTrue(response2 is Frame.Text)
                
                val receivedMessage1 = Json.decodeFromString<ChatMessage>((response1 as Frame.Text).readText())
                val receivedMessage2 = Json.decodeFromString<ChatMessage>((response2 as Frame.Text).readText())
                
                assertEquals(message1.sender, receivedMessage1.sender)
                assertEquals(message1.content, receivedMessage1.content)
                assertEquals(message1.type, receivedMessage1.type)
                
                assertEquals(message1.sender, receivedMessage2.sender)
                assertEquals(message1.content, receivedMessage2.content)
                assertEquals(message1.type, receivedMessage2.type)
            }
        }
    }

    @Test
    fun `test invalid message format`() = testApplication {
        application { configureTestChatApp() }
        // Arrange
        val invalidMessage = "This is not a valid JSON message"

        // Act & Assert
        withWebSocket("/chat") { incoming, outgoing ->
            // Send invalid message
            outgoing.send(Frame.Text(invalidMessage))

            // Should receive error message
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val errorMessage = (response as Frame.Text).readText()
            assertTrue(errorMessage.startsWith("Error:"))
        }
    }

    @Test
    fun `test media message`() = testApplication {
        application { configureTestChatApp() }
        // Arrange
        val mediaMessage = ChatMessage(
            sender = "TestUser",
            content = JsonObject(mapOf(
                "url" to JsonPrimitive("http://example.com/image.jpg"),
                "mimeType" to JsonPrimitive("image/jpeg"),
                "fileName" to JsonPrimitive("image.jpg"),
                "fileSize" to JsonPrimitive(1024)
            )),
            type = MessageType.IMAGE
        )
        val mediaMessageJson = Json.encodeToString(ChatMessage.serializer(), mediaMessage)

        // Act & Assert
        withWebSocket("/chat") { incoming, outgoing ->
            // Send media message
            outgoing.send(Frame.Text(mediaMessageJson))

            // Receive the same message back
            val response = incoming.receive()
            assertTrue(response is Frame.Text)
            val receivedMessage = Json.decodeFromString<ChatMessage>((response as Frame.Text).readText())
            
            assertEquals(mediaMessage.sender, receivedMessage.sender)
            assertEquals(mediaMessage.content, receivedMessage.content)
            assertEquals(mediaMessage.type, receivedMessage.type)
        }
    }

    @Test
    fun `test simple websocket connection`() = testApplication {
        application { configureTestChatApp() }
        val client = createClient { install(WebSockets) }
        client.webSocket("/chat") {
            // If we get here, the connection was successful
            assertTrue(true)
        }
    }
} 