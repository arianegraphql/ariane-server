package com.arianegraphql.ktx

interface SubscriptionFilter<T, S, C> {

    suspend fun test(arguments: Argument, source: S, context: C?, info: Info, item: T): Boolean
}

@JvmInline
value class FunctionalSubscriptionFilter<T, S, C>(
    private val lambda: suspend (arguments: Argument, source: S, context: C?, info: Info, item: T) -> Boolean
) : SubscriptionFilter<T, S, C> {

    override suspend fun test(
        arguments: Argument,
        source: S,
        context: C?,
        info: Info,
        item: T
    ) = lambda(arguments, source, context, info, item)
}