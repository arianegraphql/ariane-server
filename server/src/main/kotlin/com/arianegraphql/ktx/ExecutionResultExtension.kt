package com.arianegraphql.ktx

import com.arianegraphql.server.graphql.GraphQLResponse
import com.arianegraphql.server.request.*
import graphql.ExecutionResult

fun ExecutionResult.toGraphQLResponse() = GraphQLResponse(getData(), errors.map { it.toSpecification() })

fun ExecutionResult.toWebSocketPayload(operationId: String) =
    WebSocketPayload(if (errors.isEmpty()) WS_SERVER_DATA else WS_SERVER_ERROR, operationId, GraphQLResponse(
        data = getData(),
        errors = errors.map { it.toSpecification() }
    ))