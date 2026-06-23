package com.arianegraphql.server

import com.arianegraphql.ktx.argumentTypeResolver
import com.arianegraphql.ktx.toGraphQLResponse
import com.arianegraphql.server.async.*
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.dataloader.BatchLoaderRegistrar
import com.arianegraphql.server.dataloader.DataLoaderRegistryProvider
import com.arianegraphql.server.dataloader.buildRequestDataLoaderRegistry
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
import graphql.validation.QueryComplexityLimits
import kotlinx.coroutines.flow.*

class ArianeServerImpl(
    private val schema: GraphQL,
    private val scalarTypes: Map<Class<*>, Coercing<*, *>>,
    private val isPlaygroundEnabled: Boolean,
    private val contextResolver: ContextResolver,
    private val requestListener: RequestListener?,
    private val subscriptionListener: SubscriptionListener?,
    private val dataLoaderRegistryProvider: DataLoaderRegistryProvider?,
    private val jsonSerializer: JsonSerializer,
    queryComplexityLimits: QueryComplexityLimits? = null,
    private val batchLoaderRegistrar: BatchLoaderRegistrar = BatchLoaderRegistrar.EMPTY,
    playgroundRenderer: PlaygroundRenderer = PlaygroundRendererImpl(),
    requestPerformer: RequestPerformer = RequestPerformerImpl(schema, queryComplexityLimits),
    subscriptionHandler: SubscriptionHandler = SubscriptionHandlerImpl(
        jsonSerializer,
        requestPerformer,
        requestListener,
        subscriptionListener,
        dataLoaderRegistryProvider = dataLoaderRegistryProvider,
        batchLoaderRegistrar = batchLoaderRegistrar,
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

        val resolvedContext = contextResolver.resolveContext(httpRequest)

        val ctx = GraphQLContext
            .newContext()
            .of(resolvedContext)
            .argumentTypeResolver(jsonSerializer)
            .build()

        val dataLoaderRegistry = buildRequestDataLoaderRegistry(dataLoaderRegistryProvider, batchLoaderRegistrar, httpRequest, ctx)

        val response = performRequest(graphQLRequest, ctx, dataLoaderRegistry, requestListener)
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