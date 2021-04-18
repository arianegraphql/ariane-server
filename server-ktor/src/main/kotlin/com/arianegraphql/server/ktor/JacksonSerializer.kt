package com.arianegraphql.server.ktor

import com.arianegraphql.server.graphql.GraphQLRequest
import com.arianegraphql.server.graphql.GraphQLResponse
import com.arianegraphql.server.json.JsonSerializer
import com.arianegraphql.server.request.WebSocketPayload
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JacksonSerializer : JsonSerializer {
    private val jackson = jacksonObjectMapper()

    override fun parseRequest(body: String): GraphQLRequest {
        return jackson.readValue(body, GraphQLRequest::class.java)
    }

    override fun convertPayloadToRequest(payload: Any): GraphQLRequest {
        return jackson.convertValue(payload)
    }

    override fun parseWebSocketPayload(body: String): WebSocketPayload? {
        return jackson.readValue(body, WebSocketPayload::class.java)
    }

    override fun serializeResponse(response: GraphQLResponse): String {
        return jackson.writeValueAsString(response)
    }

    override fun serializeWebSocketPayload(payload: WebSocketPayload): String {
        return jackson.writeValueAsString(payload)
    }
}