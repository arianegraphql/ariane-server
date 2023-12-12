package com.arianegraphql.server.ktor.dsl

import com.arianegraphql.ktx.GraphQLSchemaDslMarker
import com.arianegraphql.ktx.makeExecutableSchema
import com.arianegraphql.server.dsl.ArianeServerBuilder
import graphql.GraphQL
import java.lang.IllegalStateException
import io.ktor.server.application.Application
import io.ktor.server.plugins.cors.*

@GraphQLSchemaDslMarker
class ArianeKtorServerBuilder : ArianeServerBuilder() {

    var ktorPlugin: KtorPlugin? = null
    var cors: (CORSConfig.() -> Unit)? = null

    fun ktorPlugin(receiver: Application.() -> Unit) {
        this.ktorPlugin = FunctionalKtorPlugin(receiver)
    }
}

fun arianeServer(builder: ArianeKtorServerBuilder.() -> Unit): ArianeKtorServerConfiguration {
    val config = ArianeKtorServerBuilder().apply(builder)

    val schema = makeExecutableSchema(config.schema ?: throw IllegalStateException("Missing schema"), config.runtimeWiringBuilder.build())

    val graphQLSchema = GraphQL
        .newGraphQL(schema)
        .build()

    return ArianeKtorServerConfiguration(
        graphQLSchema,
        config.registeredScalarTypes,
        config.host,
        config.port,
        config.path,
        config.enablePlayground,
        config.cors,
        config.contextResolver,
        config.serverListener,
        config.requestListener,
        config.subscriptionListener,
        config.ktorPlugin
    )
}