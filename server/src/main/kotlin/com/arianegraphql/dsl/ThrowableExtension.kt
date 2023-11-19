package com.arianegraphql.dsl

import com.arianegraphql.server.graphql.GraphQLResponse
import graphql.GraphqlErrorException

fun Throwable.toGraphQLResponse() = GraphQLResponse(
    null, listOf(
        GraphqlErrorException.newErrorException()
            .cause(this)
            .message(message)
            .build()
            .toSpecification()
    )
)