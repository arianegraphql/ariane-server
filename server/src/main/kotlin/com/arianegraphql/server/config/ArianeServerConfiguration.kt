package com.arianegraphql.server.config

import com.arianegraphql.server.ArianeServerImpl
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.json.JsonSerializer
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import graphql.GraphQL

open class ArianeServerConfiguration(
    val schema: GraphQL,
    val host: String,
    val port: Int,
    val path: String,
    val isPlaygroundEnabled: Boolean,
    val enableCORS: Boolean,
    val contextResolver: ContextResolver,
    val serverListener: ServerListener?,
    val requestListener: RequestListener?,
    val subscriptionListener: SubscriptionListener?
)

fun ArianeServerConfiguration.newArianeServer(jsonSerializer: JsonSerializer) =
    ArianeServerImpl(schema, isPlaygroundEnabled, contextResolver, requestListener, subscriptionListener, jsonSerializer)