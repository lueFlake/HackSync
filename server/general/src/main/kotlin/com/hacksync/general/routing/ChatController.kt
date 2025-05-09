package com.hacksync.general.routing

import com.hacksync.general.services.ChatService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.io.readByteArray
import org.slf4j.LoggerFactory
import org.koin.ktor.ext.get
import java.io.File
import java.time.Instant

private val logger = LoggerFactory.getLogger("ChatController")

fun Route.addChatRoutes() {

    route("/chat") {
        // Get message history
        get("/history") {
            val chatService = application.get<ChatService>()
            val history = chatService.getMessageHistory()
            call.respond(HttpStatusCode.OK, history)
        }

        post("/upload") {
            val multipart = call.receiveMultipart()
            var fileUrl: String? = null
            var fileName: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    fileName = part.originalFileName ?: "upload_${System.currentTimeMillis()}"
                    val dirName = "uploads/${Instant.now().toEpochMilli()}"
                    val uploadDir = File(dirName)
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs()
                    }
                    val file = File("$uploadDir/$fileName")
                    val channel: ByteReadChannel = part.provider()
                    file.outputStream().use { output ->
                        while (!channel.isClosedForRead) {
                            val buffer = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                            while (!buffer.exhausted()) {
                                val bytes = buffer.readByteArray()
                                output.write(bytes)
                            }
                        }
                    }
                    fileUrl = "/$dirName/$fileName"
                }
                part.dispose()
            }
            if (fileUrl != null) {
                call.respond(
                    mapOf(
                        "url" to fileUrl!!,
                        "fileName" to fileName
                    )
                )
            } else {
                call.respond(HttpStatusCode.BadRequest, "File upload failed")
            }
        }

        // WebSocket endpoint
        webSocket {
            val chatService = application.get<ChatService>()
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