package com.arianegraphql.server.async

import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.request.WebSocketPayload
import com.arianegraphql.server.request.WebSocketRequest
import kotlinx.coroutines.flow.Flow

interface SubscriptionHandler {
    suspend fun initSubscription(wsRequest: WebSocketRequest, contextResolver: ContextResolver<*>): Flow<String>

    suspend fun startSubscription(wsRequest: WebSocketRequest, wsPayload: WebSocketPayload, ): Flow<String>

    suspend fun stopSubscription(wsRequest: WebSocketRequest, wsPayload: WebSocketPayload): Flow<String>

    suspend fun endSubscription(sessionId: String)
}