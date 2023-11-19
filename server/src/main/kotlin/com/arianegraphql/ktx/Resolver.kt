package com.arianegraphql.ktx

import graphql.GraphQLContext
import graphql.schema.DataFetcher
import kotlinx.coroutines.runBlocking

interface Resolver<S, A> {
    suspend fun resolve(arguments: A, source: S, context: GraphQLContext, info: Info): Any?
}

@JvmInline
value class FunctionalResolver<S, A>(
    private val lambda: suspend ResolverParameters<S>.(A) -> Any?
) : Resolver<S, A> {

    override suspend fun resolve(
        arguments: A,
        source: S,
        context: GraphQLContext,
        info: Info
    ): Any? = with(ResolverParameters(source, context, info)) {
        return@with lambda(arguments)
    }
}

inline fun <S, reified A> Resolver<S, A>.toDataFetcher(): DataFetcher<Any?> = DataFetcher { env ->
    runBlocking {
        val args = env.graphQlContext.argumentTypeResolver.resolve(A::class.java, env.arguments, env.graphQlContext)
        resolve(args, env.getSource(), env.graphQlContext, env)
    }
}