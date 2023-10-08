package com.arianegraphql.ktx

import graphql.GraphQLContext

data class ResolverParameters<S>(
    val arguments: Argument,
    val source: S,
    val context: GraphQLContext,
    val info: Info,
)
