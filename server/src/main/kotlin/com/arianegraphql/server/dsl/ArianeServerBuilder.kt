package com.arianegraphql.server.dsl

import com.arianegraphql.ktx.GraphQLSchemaDslMarker
import com.arianegraphql.ktx.RuntimeWiringBuilder
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.context.FunctionalContextResolver
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import com.arianegraphql.server.request.IncomingRequest

@GraphQLSchemaDslMarker
open class ArianeServerBuilder : RuntimeWiringBuilder() {

    var host: String = DEFAULT_HOST
    var port: Int = DEFAULT_PORT
    var path: String = DEFAULT_GRAPHQL_PATH
    var schema: String? = null

    var enablePlayground = true
    var enableCORS = true

    var serverListener: ServerListener? = null
    var requestListener: RequestListener? = null
    var subscriptionListener: SubscriptionListener? = null

    var contextResolver: ContextResolver<*> = object : ContextResolver<Any> {
        override suspend fun resolveContext(request: IncomingRequest): Any? = null
    }

    fun <R : Any> context(contextResolver: ContextResolver<R>) {
        this.contextResolver = contextResolver
    }

    fun <R : Any> context(contextResolver: suspend (request: IncomingRequest) -> R?) {
        this.contextResolver = FunctionalContextResolver(contextResolver)
    }

    companion object {
        const val DEFAULT_HOST = "0.0.0.0"
        const val DEFAULT_PORT = 80
        const val DEFAULT_GRAPHQL_PATH = "/graphql"
    }
}