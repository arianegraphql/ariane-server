package com.arianegraphql.dsl

import com.arianegraphql.server.config.ArianeServerConfiguration
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import graphql.GraphQL
import graphql.schema.Coercing

class ArianeKtorServerConfiguration(
    schema: GraphQL,
    scalarTypes: Map<Class<*>, Coercing<*, *>>,
    host: String,
    port: Int,
    path: String,
    isPlaygroundEnabled: Boolean,
    enableCORS: Boolean,
    contextResolver: ContextResolver,
    serverListener: ServerListener?,
    requestListener: RequestListener?,
    subscriptionListener: SubscriptionListener?,
    val ktorPlugin: KtorPlugin?,
) : ArianeServerConfiguration(
    schema,
    scalarTypes,
    host,
    port,
    path,
    isPlaygroundEnabled,
    enableCORS,
    contextResolver,
    serverListener,
    requestListener,
    subscriptionListener
)