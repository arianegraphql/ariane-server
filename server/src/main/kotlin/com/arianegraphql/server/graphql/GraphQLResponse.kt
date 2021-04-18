package com.arianegraphql.server.graphql

data class GraphQLResponse(
    val data: Any?,
    val errors: List<Map<String, Any?>>?
)