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
import java.util.UUID

private val logger = LoggerFactory.getLogger("ChatController")

fun Route.addChatRoutes() {
    route("/chat") {
        // Get message history for specific hackathon
        get("/history/{hackathonId}") {
            val hackathonId = call.parameters["hackathonId"]?.let { 
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            if (hackathonId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid hackathon ID")
                return@get
            }
            
            val chatService = application.get<ChatService>()
            val history = chatService.getMessageHistory(hackathonId)
            call.respond(HttpStatusCode.OK, history)
        }

        post("/upload/{hackathonId}") {
            val hackathonId = call.parameters["hackathonId"]?.let { 
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            if (hackathonId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid hackathon ID")
                return@post
            }

            try {
                val multipart = call.receiveMultipart()
                var fileUrl: String? = null
                var fileName: String? = null

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        fileName = part.originalFileName ?: "upload_${System.currentTimeMillis()}"
                        val dirName = "uploads/${hackathonId}/${Instant.now().toEpochMilli()}"
                        val uploadDir = File(dirName)
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs()
                        }
                        val file = File("$dirName/$fileName")
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

                if (fileUrl != null && fileName != null) {
                    call.respond(
                        mapOf(
                            "url" to fileUrl,
                            "fileName" to fileName
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.BadRequest, "No file was uploaded")
                }
            } catch (e: Exception) {
                logger.error("File upload failed", e)
                call.respond(HttpStatusCode.InternalServerError, "File upload failed: ${e.message}")
            }
        }

        // WebSocket endpoint for specific hackathon
        webSocket("/{hackathonId}") {
            val hackathonId = call.parameters["hackathonId"]?.let { 
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
            if (hackathonId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid hackathon ID"))
                return@webSocket
            }

            val chatService = application.get<ChatService>()
            val thisConnection = Connection(this, hackathonId)
            
            try {
                chatService.addConnection(thisConnection)
                logger.info("New WebSocket connection established for hackathon $hackathonId")
                
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            try {
                                val message = chatService.processMessage(text, hackathonId)
                                chatService.broadcastMessage(message, hackathonId)
                            } catch (e: Exception) {
                                logger.error("Error processing message: ${e.message}")
                                send("Error: ${e.message}")
                            }
                        }
                        is Frame.Binary -> {
                            logger.warn("Binary frames are not supported")
                            send("Error: Binary frames are not supported")
                        }
                        is Frame.Close -> {
                            logger.info("Received close frame: ${frame.readReason()}")
                            break
                        }
                        else -> {
                            logger.warn("Unsupported frame type: ${frame.frameType}")
                        }
                    }
                }
            } catch (e: ClosedSendChannelException) {
                logger.info("Channel closed for ${thisConnection.name}")
            } catch (e: Exception) {
                logger.error("Error in WebSocket connection: ${e.message}")
            } finally {
                chatService.removeConnection(thisConnection)
                logger.info("WebSocket connection closed for hackathon $hackathonId")
            }
        }
    }
}

data class Connection(
    val session: DefaultWebSocketSession,
    val hackathonId: UUID,
    val name: String = "User${System.currentTimeMillis()}"
) 