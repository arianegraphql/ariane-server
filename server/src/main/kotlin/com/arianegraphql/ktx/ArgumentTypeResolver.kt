package com.arianegraphql.ktx

import graphql.GraphQLContext
import graphql.schema.Coercing

interface ArgumentTypeResolver {

    fun <T> resolve(type: Class<T>, value: Map<String, Any>, graphQLContext: GraphQLContext): T

    fun registerResolverType(type: Class<*>, coercing: Coercing<*, *>)
}

val GraphQLContext.argumentTypeResolver: ArgumentTypeResolver
    get() = get("com.arianegraphql:ArgumentTypeResolver")

internal fun GraphQLContext.Builder.argumentTypeResolver(it: ArgumentTypeResolver): GraphQLContext.Builder =
    put("com.arianegraphql:ArgumentTypeResolver", it)