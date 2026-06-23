package com.arianegraphql.server.graphql

data class GraphQLRequest(
    val operationName: String? = null,
    val query: String,
    val variables: Map<String, Any?>? = null,
    val extensions: Any? = null
)