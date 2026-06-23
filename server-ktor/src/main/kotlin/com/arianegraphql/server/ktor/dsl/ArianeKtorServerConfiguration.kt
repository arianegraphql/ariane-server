package com.arianegraphql.server.ktor.dsl

import com.arianegraphql.server.config.ArianeServerConfiguration
import com.arianegraphql.server.context.ContextResolver
import com.arianegraphql.server.dataloader.BatchLoaderRegistrar
import com.arianegraphql.server.dataloader.DataLoaderRegistryProvider
import com.arianegraphql.server.listener.RequestListener
import com.arianegraphql.server.listener.ServerListener
import com.arianegraphql.server.listener.SubscriptionListener
import graphql.GraphQL
import graphql.schema.Coercing
import graphql.validation.QueryComplexityLimits
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.*

class ArianeKtorServerConfiguration(
    schema: GraphQL,
    scalarTypes: Map<Class<*>, Coercing<*, *>>,
    host: String,
    port: Int,
    path: String,
    isPlaygroundEnabled: Boolean,
    val corsConfig: (CORSConfig.() -> Unit)?,
    contextResolver: ContextResolver,
    serverListener: ServerListener?,
    requestListener: RequestListener?,
    subscriptionListener: SubscriptionListener?,
    dataLoaderRegistryProvider: DataLoaderRegistryProvider?,
    val ktorPlugin: KtorPlugin?,
    queryComplexityLimits: QueryComplexityLimits? = null,
    batchLoaderRegistrar: BatchLoaderRegistrar = BatchLoaderRegistrar.EMPTY,
) : ArianeServerConfiguration(
    schema,
    scalarTypes,
    host,
    port,
    path,
    isPlaygroundEnabled,
    corsConfig != null,
    contextResolver,
    serverListener,
    requestListener,
    subscriptionListener,
    dataLoaderRegistryProvider,
    queryComplexityLimits,
    batchLoaderRegistrar
)