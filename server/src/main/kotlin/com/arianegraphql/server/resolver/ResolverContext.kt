package com.arianegraphql.server.resolver

import com.arianegraphql.ktx.Info
import graphql.GraphQLContext

data class ResolverContext<S>(
    val source: S,
    val context: GraphQLContext,
    val info: Info,
)