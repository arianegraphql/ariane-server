package com.arianegraphql.server.dsl

import com.arianegraphql.ktx.GraphQLSchemaDslMarker
import com.arianegraphql.ktx.RuntimeWiringBuilder
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.context.FunctionalContextResolver
import com.arianegraphql.server.dataloader.DataLoaderRegistryProvider
import com.arianegraphql.server.dataloader.FunctionalDataLoaderRegistryProvider
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import com.arianegraphql.server.request.IncomingRequest
import graphql.GraphQLContext
import graphql.validation.QueryComplexityLimits
import org.dataloader.DataLoaderRegistry

@GraphQLSchemaDslMarker
open class ArianeServerBuilder : RuntimeWiringBuilder() {

    var host: String = DEFAULT_HOST
    var port: Int = DEFAULT_PORT
    var path: String = DEFAULT_GRAPHQL_PATH
    var schema: String? = null

    var enablePlayground = true

    /**
     * Query complexity limits applied to every request.
     *
     * Since graphql-java 26, queries are validated against default limits
     * (max depth 100, max fields count 100,000). Set this to
     * [QueryComplexityLimits.NONE] to disable the validation, or build custom
     * limits with `QueryComplexityLimits.newLimits()`. Leave null to keep the
     * graphql-java defaults. A [QueryComplexityLimits] entry provided per
     * request through the context resolver takes precedence.
     */
    var queryComplexityLimits: QueryComplexityLimits? = null

    var serverListener: ServerListener? = null
    var requestListener: RequestListener? = null
    var subscriptionListener: SubscriptionListener? = null

    var contextResolver: ContextResolver = object : ContextResolver {
        override suspend fun resolveContext(request: IncomingRequest): GraphQLContext = GraphQLContext.of(emptyMap<Any, Any>())
    }

    var dataLoaderRegistryProvider: DataLoaderRegistryProvider? = null

    fun context(contextResolver: ContextResolver) {
        this.contextResolver = contextResolver
    }

    fun context(contextResolver: suspend (request: IncomingRequest) -> GraphQLContext) {
        this.contextResolver = FunctionalContextResolver(contextResolver)
    }

    fun dataLoaders(provider: DataLoaderRegistryProvider) {
        this.dataLoaderRegistryProvider = provider
    }

    fun dataLoaders(provider: suspend (request: IncomingRequest, context: GraphQLContext) -> DataLoaderRegistry) {
        this.dataLoaderRegistryProvider = FunctionalDataLoaderRegistryProvider(provider)
    }

    companion object {
        const val DEFAULT_HOST = "0.0.0.0"
        const val DEFAULT_PORT = 80
        const val DEFAULT_GRAPHQL_PATH = "/graphql"
    }
}