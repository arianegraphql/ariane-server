package com.arianegraphql.server.context

import com.arianegraphql.server.request.IncomingRequest
import graphql.GraphQLContext

interface ContextResolver {
    suspend fun resolveContext(request: IncomingRequest): GraphQLContext
}

@JvmInline value class FunctionalContextResolver(
    private val lambda: suspend (request: IncomingRequest) -> GraphQLContext
) : ContextResolver {

    override suspend fun resolveContext(request: IncomingRequest): GraphQLContext = lambda(request)
}