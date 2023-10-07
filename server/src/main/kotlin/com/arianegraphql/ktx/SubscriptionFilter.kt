package com.arianegraphql.ktx

import graphql.GraphQLContext

interface SubscriptionFilter<T, S> {

    suspend fun test(arguments: Argument, source: S, context: GraphQLContext, info: Info, item: T): Boolean
}

@JvmInline
value class FunctionalSubscriptionFilter<T, S>(
    private val lambda: suspend (arguments: Argument, source: S, context: GraphQLContext, info: Info, item: T) -> Boolean
) : SubscriptionFilter<T, S> {

    override suspend fun test(
        arguments: Argument,
        source: S,
        context: GraphQLContext,
        info: Info,
        item: T
    ) = lambda(arguments, source, context, info, item)
}