package com.arianegraphql.ktx

import graphql.GraphQLContext

interface ArgumentTypeResolver {

    fun <T> resolve(type: Class<T>, value: Map<String, Any>): T
}

val GraphQLContext.argumentTypeResolver: ArgumentTypeResolver
    get() = get("com.arianegraphql:ArgumentTypeResolver")

internal fun GraphQLContext.Builder.argumentTypeResolver(it: ArgumentTypeResolver): GraphQLContext.Builder =
    put("com.arianegraphql:ArgumentTypeResolver", it)