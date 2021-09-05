package com.arianegraphql.ktx

import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking

interface Resolver<S, C> {
    suspend fun resolve(arguments: Argument, source: S, context: C?, info: Info): Any?
}

@JvmInline value class FunctionalResolver<S, C>(
    private val lambda: suspend (arguments: Argument, source: S, context: C?, info: Info) -> Any?
) : Resolver<S, C> {

    override suspend fun resolve(
        arguments: Argument,
        source: S,
        context: C?,
        info: Info
    ) = lambda(arguments, source, context, info)
}

internal fun <S, C> Resolver<S, C>.toDataFetcher(): DataFetcher<Any?> = DataFetcher { env ->
    runBlocking {
        resolve(DataFetchingArgument(env), env.getSource(), env.getContext(), env)
    }
}