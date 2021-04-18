package com.arianegraphql.server.json

import com.arianegraphql.server.graphql.GraphQLRequest
import com.arianegraphql.server.graphql.GraphQLResponse
import com.arianegraphql.server.request.WebSocketPayload

interface JsonSerializer {

    fun parseRequest(body: String): GraphQLRequest

    fun convertPayloadToRequest(payload: Any): GraphQLRequest

    fun parseWebSocketPayload(body: String): WebSocketPayload?

    fun serializeResponse(response: GraphQLResponse): String

    fun serializeWebSocketPayload(payload: WebSocketPayload): String
}