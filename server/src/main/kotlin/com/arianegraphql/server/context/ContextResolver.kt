package com.arianegraphql.server.context

import com.arianegraphql.server.request.IncomingRequest

interface ContextResolver<R: Any> {
    suspend fun resolveContext(request: IncomingRequest): R?
}

@JvmInline value class FunctionalContextResolver<R: Any>(
    private val lambda: suspend (request: IncomingRequest) -> R?
) : ContextResolver<R> {

    override suspend fun resolveContext(request: IncomingRequest): R? = lambda(request)
}