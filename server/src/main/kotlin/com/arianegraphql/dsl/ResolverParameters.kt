package com.arianegraphql.dsl

import graphql.GraphQLContext

data class ResolverParameters<S>(
    val source: S,
    val context: GraphQLContext,
    val info: Info,
)
