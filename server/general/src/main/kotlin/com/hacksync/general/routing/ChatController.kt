package com.hacksync.general.routing

import com.hacksync.general.model.ChatMessage
import com.hacksync.general.services.ChatService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.scope
import java.util.*
import kotlin.collections.LinkedHashSet

private val logger = LoggerFactory.getLogger("ChatController")

fun Route.addChatRoutes() {

    route("/chat") {
        // Get message history
        get("/history") {
            val chatService = call.scope.get<ChatService>()
            val history = chatService.getMessageHistory()
            call.respond(HttpStatusCode.OK, history)
        }

        // WebSocket endpoint
        webSocket {
            val chatService = call.scope.get<ChatService>()
            val thisConnection = Connection(this)
            chatService.addConnection(thisConnection)
            
            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            try {
                                val message = chatService.processMessage(text)
                                chatService.broadcastMessage(message)
                            } catch (e: Exception) {
                                logger.error("Error processing message: ${e.message}")
                                send("Error: ${e.message}")
                            }
                        }
                        is Frame.Binary -> {
                            logger.warn("Binary frames are not supported. Please use text frames with media URLs.")
                            send("Error: Binary frames are not supported. Please use text frames with media URLs.")
                        }
                        else -> {} // Ignore other frame types
                    }
                }
            } catch (e: ClosedSendChannelException) {
                logger.info("Channel closed for ${thisConnection.name}")
            } catch (e: Exception) {
                logger.error("Error in WebSocket connection: ${e.message}")
            } finally {
                chatService.removeConnection(thisConnection)
            }
        }
    }
}

data class Connection(
    val session: DefaultWebSocketSession,
    val name: String = "User${System.currentTimeMillis()}"
) 