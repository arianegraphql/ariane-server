package com.arianegraphql.server.ktor

import com.arianegraphql.server.ArianeServer
import com.arianegraphql.server.request.WebSocketRequest
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import java.util.*

context(ArianeServer)
suspend fun WebSocketServerSession.handleGraphQLSubscription() {
    val sessionId = UUID.randomUUID().toString()

    try {
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val payload = frame.readText()
            val headers = call.request.headers.toMap()
            val request = WebSocketRequest(sessionId, payload, headers)

            launch {
                handleWebSocketMessage(request).collect { response ->
                    outgoing.send(Frame.Text(response))
                }
            }

        }
    } finally {
        handleClosedWebSocket(sessionId)
    }
}