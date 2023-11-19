package com.arianegraphql.server

import com.arianegraphql.server.request.*
import com.arianegraphql.server.response.*
import kotlinx.coroutines.flow.Flow

interface ArianeServer {

    fun initializeServer()

    suspend fun handleHttpRequest(httpRequest: HttpRequest, onResponse: suspend (HttpResponse) -> Unit)

    suspend fun handleWebSocketMessage(wsRequest: WebSocketRequest): Flow<String>

    suspend fun handleClosedWebSocket(sessionId: String)
}
