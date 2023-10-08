package com.arianegraphql.ktx

import graphql.GraphQLContext
import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking

interface Resolver<S> {
    suspend fun resolve(arguments: Argument, source: S, context: GraphQLContext, info: Info): Any?
}

@JvmInline value class FunctionalResolver<S>(
    private val lambda: suspend (parameters: ResolverParameters<S>) -> Any?
) : Resolver<S> {

    override suspend fun resolve(
        arguments: Argument,
        source: S,
        context: GraphQLContext,
        info: Info
    ) = lambda(ResolverParameters(arguments, source, context, info))
}

internal fun <S> Resolver<S>.toDataFetcher(): DataFetcher<Any?> = DataFetcher { env ->
    runBlocking {
        env.graphQlContext
        resolve(DataFetchingArgument(env), env.getSource(), env.graphQlContext, env)
    }
}