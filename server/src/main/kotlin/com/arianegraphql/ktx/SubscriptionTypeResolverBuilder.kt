package com.arianegraphql.ktx

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher

@GraphQLSchemaDslMarker
class SubscriptionTypeResolverBuilder : TypeResolverBuilder() {

    fun <T> resolve(field: String, publisher: Publisher<T>) = resolve(field) { _, _: Any?, _: Any?, _ -> publisher }

    fun <T : Any> resolve(field: String, flow: Flow<T>) = resolve(field, flow.asPublisher())

    fun <T : Any, S, C> resolve(field: String, flow: Flow<T>, predicate: SubscriptionFilter<T, S, C>) =
        resolve(field) { arguments: Argument, source: S, context: C?, info: Info ->
            flow.filter {
                predicate.test(arguments, source, context, info, it)
            }.asPublisher()
        }

    fun <T : Any, S, C> resolve(
        field: String,
        flow: Flow<T>,
        predicate: suspend (arguments: Argument, source: S, context: C?, info: Info, item: T) -> Boolean
    ) = resolve(field, flow, FunctionalSubscriptionFilter(predicate))
}