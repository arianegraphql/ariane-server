package com.arianegraphql.server.async

import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.extensions.toWebSocketPayload
import com.arianegraphql.server.graphql.RequestPerformer
import com.arianegraphql.server.json.JsonSerializer
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.SubscriptionListener
import com.arianegraphql.server.request.*
import com.arianegraphql.server.store.OperationStore
import com.arianegraphql.server.store.OperationStoreImpl
import com.arianegraphql.server.store.SessionStore
import com.arianegraphql.server.store.SessionStoreImpl
import graphql.ExecutionResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.Publisher

class SubscriptionHandlerImpl(
    jsonSerializer: JsonSerializer,
    requestPerformer: RequestPerformer,
    private val requestListener: RequestListener?,
    private val subscriptionListener: SubscriptionListener?,
    private val sessionStore: SessionStore = SessionStoreImpl(),
    private val operationStore: OperationStore = OperationStoreImpl()
) : SubscriptionHandler, JsonSerializer by jsonSerializer, RequestPerformer by requestPerformer {

    override suspend fun initSubscription(wsRequest: WebSocketRequest, contextResolver: ContextResolver<*>): Flow<String> {
        subscriptionListener?.onNewConnection(wsRequest.sessionId)
        val context = contextResolver.resolveContext(wsRequest)
        sessionStore.saveContext(wsRequest.sessionId, context)
        subscriptionListener?.onConnected(wsRequest.sessionId, context)

        return flowOf(serializeWebSocketPayload(connectionAck))
    }

    override suspend fun startSubscription(wsRequest: WebSocketRequest, wsPayload: WebSocketPayload): Flow<String> {
        val payload = wsPayload.payload ?: return flowOf(serializeWebSocketPayload(connectionError))
        val operationId = wsPayload.id ?: return flowOf(serializeWebSocketPayload(connectionError))
        val graphQLRequest = convertPayloadToRequest(payload)

        return flow {
            operationStore.startOperation(wsRequest.sessionId, operationId) {
                val context = sessionStore.getContext(wsRequest.sessionId)
                subscriptionListener?.onStartSubscription(wsRequest.sessionId, context, operationId, graphQLRequest)

                performRequest(graphQLRequest, context, requestListener)
                    .map { it.getData<Publisher<ExecutionResult>>().asFlow() }
                    .getOrElse { emptyFlow() }
                    .collect { emit(serializeWebSocketPayload(it.toWebSocketPayload(wsPayload.id))) }
            }
        }
    }

    override suspend fun stopSubscription(wsRequest: WebSocketRequest, wsPayload: WebSocketPayload): Flow<String> {
        val operationId = wsPayload.id ?: return flowOf(serializeWebSocketPayload(connectionError))

        operationStore.stopOperation(wsRequest.sessionId, operationId)
        val context = sessionStore.getContext(wsRequest.sessionId)
        subscriptionListener?.onStopSubscription(wsRequest.sessionId, context, operationId)

        return flowOf(serializeWebSocketPayload(connectionComplete(operationId)))
    }

    override suspend fun endSubscription(sessionId: String) {
        if (operationStore.hasOperationForSessionId(sessionId)) {
            val context = sessionStore.getContext(sessionId)
            subscriptionListener?.onCloseConnection(sessionId, context)

            sessionStore.clearContext(sessionId)

            operationStore.stopAllOperations(sessionId)
        }
    }
}