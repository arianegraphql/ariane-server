package com.arianegraphql.ktx

import graphql.GraphQLContext
import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking

interface Resolver<S, A, R: Any?> {
    suspend fun resolve(arguments: A, source: S, context: GraphQLContext, info: Info): R
}

@JvmInline
value class FunctionalResolver<S, A, R: Any?>(
    private val lambda: suspend ResolverParameters<S>.(A) -> R
) : Resolver<S, A, R> {

    override suspend fun resolve(
        arguments: A,
        source: S,
        context: GraphQLContext,
        info: Info
    ): R = with(ResolverParameters(source, context, info)) {
        return@with lambda(arguments)
    }
}

inline fun <S, reified A, R: Any?> Resolver<S, A, R>.toDataFetcher(): DataFetcher<R> = DataFetcher { env ->
    runBlocking {
        val args = env.graphQlContext.argumentTypeResolver.resolve(A::class.java, env.arguments, env.graphQlContext)
        resolve(args, env.getSource(), env.graphQlContext, env)
    }
}