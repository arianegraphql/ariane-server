package com.arianegraphql.ktx

import graphql.GraphqlErrorException

fun Throwable.toGraphQLError() = GraphqlErrorException.newErrorException()
    .cause(this)
    .message(message)
    .build()