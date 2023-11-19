package com.arianegraphql.server.config

import com.arianegraphql.dsl.KtorPlugins
import com.arianegraphql.server.ArianeServerImpl
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.json.JsonSerializer
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import graphql.GraphQL
import graphql.schema.Coercing

open class ArianeServerConfiguration(
    val schema: GraphQL,
    val scalarTypes: Map<Class<*>, Coercing<*, *>>,
    val host: String,
    val port: Int,
    val path: String,
    val isPlaygroundEnabled: Boolean,
    val enableCORS: Boolean,
    val contextResolver: ContextResolver,
    val serverListener: ServerListener?,
    val requestListener: RequestListener?,
    val subscriptionListener: SubscriptionListener?,
    val ktorPlugins: KtorPlugins?,
)

fun ArianeServerConfiguration.newArianeServer(jsonSerializer: JsonSerializer) =
    ArianeServerImpl(schema, scalarTypes, isPlaygroundEnabled, contextResolver, requestListener, subscriptionListener, jsonSerializer)