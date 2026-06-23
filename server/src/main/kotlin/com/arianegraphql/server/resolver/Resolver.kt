package com.arianegraphql.server.resolver

import com.arianegraphql.ktx.Info
import com.arianegraphql.ktx.argumentTypeResolver
import graphql.GraphQLContext
import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking

interface Resolver<S, A, R: Any?> {
    suspend fun resolve(arguments: A, source: S, context: GraphQLContext, info: Info): R
}

@JvmInline
value class FunctionalResolver<S, A, R: Any?>(
    private val lambda: suspend ResolverContext<S>.(A) -> R
) : Resolver<S, A, R> {

    override suspend fun resolve(
        arguments: A,
        source: S,
        context: GraphQLContext,
        info: Info
    ): R = with(ResolverContext(source, context, info)) {
        return@with lambda(arguments)
    }
}

inline fun <S: Any?, reified A, R: Any?> Resolver<S, A, R>.toDataFetcher(): DataFetcher<R> = toDataFetcher(A::class.java)

fun <S: Any?, A, R: Any?> Resolver<S, A, R>.toDataFetcher(argumentType: Class<A>): DataFetcher<R> = DataFetcher { env ->
    runBlocking {
        val args = env.graphQlContext.argumentTypeResolver.resolve(argumentType, env.arguments, env.graphQlContext)
        @Suppress("UNCHECKED_CAST")
        val source = env.getSource<Any>() as S
        resolve(args, source, env.graphQlContext, env)
    }
}