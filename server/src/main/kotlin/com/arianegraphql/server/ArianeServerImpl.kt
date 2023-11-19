package com.arianegraphql.server

import com.arianegraphql.dsl.argumentTypeResolver
import com.arianegraphql.dsl.toGraphQLResponse
import com.arianegraphql.server.async.*
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.graphql.*
import com.arianegraphql.server.json.JsonSerializer
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.SubscriptionListener
import com.arianegraphql.server.playground.*
import com.arianegraphql.server.request.*
import com.arianegraphql.server.response.HttpResponse
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.schema.Coercing
import kotlinx.coroutines.flow.*

class ArianeServerImpl(
    private val schema: GraphQL,
    private val scalarTypes: Map<Class<*>, Coercing<*, *>>,
    private val isPlaygroundEnabled: Boolean,
    private val contextResolver: ContextResolver,
    private val requestListener: RequestListener?,
    private val subscriptionListener: SubscriptionListener?,
    private val jsonSerializer: JsonSerializer,
    playgroundRenderer: PlaygroundRenderer = PlaygroundRendererImpl(),
    requestPerformer: RequestPerformer = RequestPerformerImpl(schema),
    subscriptionHandler: SubscriptionHandler = SubscriptionHandlerImpl(
        jsonSerializer,
        requestPerformer,
        requestListener,
        subscriptionListener
    )
) : ArianeServer, PlaygroundRenderer by playgroundRenderer, JsonSerializer by jsonSerializer,
    RequestPerformer by requestPerformer,
    SubscriptionHandler by subscriptionHandler {

    override fun initializeServer() {
        scalarTypes.forEach {
            jsonSerializer.registerResolverType(it.key, it.value)
        }
    }

    override suspend fun handleHttpRequest(httpRequest: HttpRequest, onResponse: suspend (HttpResponse) -> Unit) {
        requestListener?.onReceived(httpRequest)
        if (isPlaygroundEnabled && isPlaygroundRequest(httpRequest)) {
            onResponse(renderExplorer())
            return
        }

        val graphQLRequest = parseRequest(httpRequest.body)

        requestListener?.onParsed(graphQLRequest)

        val ctx = GraphQLContext
            .newContext()
            .of(contextResolver.resolveContext(httpRequest))
            .argumentTypeResolver(jsonSerializer)
            .build()

        val response = performRequest(graphQLRequest, ctx, requestListener)
            .map { executionResult ->
                val response = executionResult.toGraphQLResponse()
                requestListener?.onExecuted(response)
                response
            }
            .getOrElse { error ->
                val response = error.toGraphQLResponse()
                requestListener?.onError(error)
                response
            }
            .let { HttpResponse(serializeResponse(it), CONTENT_TYPE) }

        onResponse(response)
        requestListener?.onResponded()
    }

    override suspend fun handleWebSocketMessage(wsRequest: WebSocketRequest): Flow<String> {
        val wsPayload = parseWebSocketPayload(wsRequest.body) ?: run {
            return flowOf(serializeWebSocketPayload(connectionError))
        }

        return runCatching {
            when {
                wsPayload.isConnectionInit -> initSubscription(wsRequest, contextResolver)
                wsPayload.isStart -> startSubscription(wsRequest, wsPayload)
                wsPayload.isStop -> stopSubscription(wsRequest, wsPayload)
                wsPayload.isConnectionTerminate -> {
                    endSubscription(wsRequest.sessionId)
                    emptyFlow()
                }
                else -> flowOf(serializeWebSocketPayload(connectionError))
            }
        }.getOrElse {
            flowOf(serializeWebSocketPayload(connectionError))
        }
    }

    override suspend fun handleClosedWebSocket(sessionId: String) = endSubscription(sessionId)

    companion object {
        const val CONTENT_TYPE = "application/json"
    }
}