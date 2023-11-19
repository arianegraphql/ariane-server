package com.arianegraphql.dsl

import com.arianegraphql.server.config.ArianeServerConfiguration
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.context.FunctionalContextResolver
import com.arianegraphql.server.listener.*
import com.arianegraphql.server.request.IncomingRequest
import graphql.GraphQL
import graphql.GraphQLContext
import io.ktor.server.application.*
import java.lang.IllegalStateException

@GraphQLSchemaDslMarker
open class ArianeServerBuilder : RuntimeWiringBuilder() {

    var host: String = DEFAULT_HOST
    var port: Int = DEFAULT_PORT
    var path: String = DEFAULT_GRAPHQL_PATH
    var schema: String? = null

    var enablePlayground = true
    var enableCORS = true

    internal var onServerStarted: OnServerStarted = { _, _, _ -> }
    internal var onServerStopped: OnServerStopped = { }
    var requestListener: RequestListener? = null
    var subscriptionListener: SubscriptionListener? = null

    internal var ktorPlugins: KtorPlugins? = null

    var contextResolver: ContextResolver = object : ContextResolver {
        override suspend fun resolveContext(request: IncomingRequest): GraphQLContext =
            GraphQLContext.of(emptyMap<Any, Any>())
    }

    fun context(contextResolver: ContextResolver) {
        this.contextResolver = contextResolver
    }

    fun context(contextResolver: suspend (request: IncomingRequest) -> GraphQLContext) {
        this.contextResolver = FunctionalContextResolver(contextResolver)
    }

    fun plugins(receiver: Application.() -> Unit) {
        this.ktorPlugins = FunctionalKtorPlugins(receiver)
    }

    fun onStart(block: OnServerStarted) {
        onServerStarted = block
    }

    fun onStop(block: OnServerStopped) {
        onServerStopped = block
    }

    companion object {
        const val DEFAULT_HOST = "0.0.0.0"
        const val DEFAULT_PORT = 80
        const val DEFAULT_GRAPHQL_PATH = "/graphql"
    }
}

fun arianeServer(builder: ArianeServerBuilder.() -> Unit): ArianeServerConfiguration {
    val config = ArianeServerBuilder().apply(builder)

    val schema = makeExecutableSchema(
        config.schema ?: throw IllegalStateException("Missing schema"),
        config.runtimeWiringBuilder.build()
    )

    val graphQLSchema = GraphQL
        .newGraphQL(schema)
        .build()

    return ArianeServerConfiguration(
        graphQLSchema,
        config.registeredScalarTypes,
        config.host,
        config.port,
        config.path,
        config.enablePlayground,
        config.enableCORS,
        config.contextResolver,
        ServerListener(config.onServerStarted, config.onServerStopped),
        config.requestListener,
        config.subscriptionListener,
        config.ktorPlugins
    )
}