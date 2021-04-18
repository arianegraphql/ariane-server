package com.arianegraphql.server.graphql

data class GraphQLRequest(
    val operationName: String?,
    val query: String,
    val variables: Map<String, Any?>,
    val extensions: Any?
)