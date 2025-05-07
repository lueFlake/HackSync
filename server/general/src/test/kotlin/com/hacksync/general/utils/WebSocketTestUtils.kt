package com.hacksync.general.utils

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import kotlin.time.Duration.Companion.seconds

suspend fun ApplicationTestBuilder.withWebSocket(
    path: String,
    block: suspend (incoming: Channel<Frame>, outgoing: Channel<Frame>) -> Unit
) = coroutineScope {
    val incoming = Channel<Frame>()
    val outgoing = Channel<Frame>()

    val client = createClient { install(WebSockets) }

    client.webSocket(path) {
        val receiveFrames = launch {
            try {
                for (frame in incoming) {
                    send(frame)
                }
            } catch (_: Exception) {}
        }
        val sendFrames = launch {
            try {
                for (frame in outgoing) {
                    send(frame)
                }
            } catch (_: Exception) {}
        }
        withTimeout(5.seconds) {
            block(incoming, outgoing)
        }
        receiveFrames.cancel()
        sendFrames.cancel()
    }
    incoming.close()
    outgoing.close()
} 