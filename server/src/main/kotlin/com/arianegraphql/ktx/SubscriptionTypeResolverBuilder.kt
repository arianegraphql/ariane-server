package com.arianegraphql.ktx

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher

@GraphQLSchemaDslMarker
class SubscriptionTypeResolverBuilder : TypeResolverBuilder<GraphQLTypes.Subscription>() {

    inline fun <T : Any, reified A> resolve(field: String, flow: Flow<T>) = resolve<T, A>(field, flow.asPublisher())

    inline fun <T, reified A> resolve(field: String, publisher: Publisher<T>) = resolve<A>(field) { publisher }

    inline fun <T : Any, reified A> resolve(
        field: String,
        flow: Flow<T>,
        predicate: SubscriptionFilter<T, GraphQLTypes.Subscription, A>
    ) = resolve<A>(field) { arguments ->
        flow.filter { item ->
            predicate.test(arguments, source, context, info, item)
        }.asPublisher()
    }

    inline fun <T : Any, reified A> resolve(
        field: String,
        flow: Flow<T>,
        noinline predicate: suspend ResolverParameters<GraphQLTypes.Subscription>.(arguments: A, item: T) -> Boolean
    ) = resolve(field, flow, FunctionalSubscriptionFilter(predicate))
}