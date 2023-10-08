package com.arianegraphql.ktx

import graphql.GraphQLContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher

@GraphQLSchemaDslMarker
class SubscriptionTypeResolverBuilder : TypeResolverBuilder<GraphQLTypes.Subscription>() {

    fun <T> resolve(field: String, publisher: Publisher<T>) = resolve(field) { publisher }

    fun <T : Any> resolve(field: String, flow: Flow<T>) = resolve(field, flow.asPublisher())

    fun <T : Any> resolve(field: String, flow: Flow<T>, predicate: SubscriptionFilter<T, GraphQLTypes.Subscription>) =
        resolve(field) {
            flow.filter {
                predicate.test(arguments, source, context, info, it)
            }.asPublisher()
        }

    fun <T : Any> resolve(
        field: String,
        flow: Flow<T>,
        predicate: suspend (arguments: Argument, source: GraphQLTypes.Subscription, context: GraphQLContext, info: Info, item: T) -> Boolean
    ) = resolve(field, flow, FunctionalSubscriptionFilter(predicate))
}