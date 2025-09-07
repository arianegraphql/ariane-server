package com.arianegraphql.ktx

import com.arianegraphql.server.resolver.ResolverContext
import graphql.GraphQLContext

interface SubscriptionFilter<T, S, A> {

    suspend fun test(arguments: A, source: S, context: GraphQLContext, info: Info, item: T): Boolean
}

@JvmInline
value class FunctionalSubscriptionFilter<T, S, A>(
    private val lambda: suspend ResolverContext<S>.(arguments: A, item: T) -> Boolean
) : SubscriptionFilter<T, S, A> {

    override suspend fun test(
        arguments: A,
        source: S,
        context: GraphQLContext,
        info: Info,
        item: T
    ) = with(ResolverContext(source, context, info)) {
        lambda(arguments, item)
    }
}