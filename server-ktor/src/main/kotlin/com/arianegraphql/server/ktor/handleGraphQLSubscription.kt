package com.arianegraphql.server.ktor

import com.arianegraphql.server.ArianeServer
import com.arianegraphql.server.request.WebSocketRequest
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

suspend fun WebSocketServerSession.handleGraphQLSubscription(arianeServer: ArianeServer) {
    val sessionId = UUID.randomUUID().toString()

    try {
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val payload = frame.readText()
            val headers = call.request.headers.toMap()
            val request = WebSocketRequest(sessionId, payload, headers)

            launch {
                arianeServer.handleWebSocketMessage(request).collect { response ->
                    outgoing.send(Frame.Text(response))
                }
            }

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}