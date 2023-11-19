package com.arianegraphql.dsl

import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
class RootResolverBuilder {
    private val rootResolver = mutableListOf<TypeRuntimeWiring.Builder>()

    fun <S> type(typeName: String, builder: TypeResolverBuilder<S>.() -> Unit) {
        rootResolver.add(TypeResolverBuilder<S>().apply(builder).build(typeName))
    }

    fun RootResolverBuilder.Mutation(builder: TypeResolverBuilder<GraphQLTypes.Mutation>.() -> Unit) =
        type(TYPE_MUTATION_NAME, builder)

    fun RootResolverBuilder.Query(builder: TypeResolverBuilder<GraphQLTypes.Query>.() -> Unit) =
        type(TYPE_QUERY_NAME, builder)

    fun RootResolverBuilder.Subscription(builder: SubscriptionTypeResolverBuilder.() -> Unit) {
        rootResolver.add(SubscriptionTypeResolverBuilder().apply(builder).build(TYPE_SUBSCRIPTION_NAME))
    }

    fun build() = rootResolver
}


const val TYPE_MUTATION_NAME = "Mutation"
const val TYPE_QUERY_NAME = "Query"
const val TYPE_SUBSCRIPTION_NAME = "Subscription"

object GraphQLTypes {
    object Mutation

    object Query

    object Subscription
}