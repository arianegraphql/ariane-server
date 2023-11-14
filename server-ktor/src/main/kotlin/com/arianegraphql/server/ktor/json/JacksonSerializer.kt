package com.arianegraphql.server.ktor.json

import com.arianegraphql.server.graphql.GraphQLRequest
import com.arianegraphql.server.graphql.GraphQLResponse
import com.arianegraphql.server.json.JsonSerializer
import com.arianegraphql.server.request.WebSocketPayload
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import graphql.schema.Coercing

object JacksonSerializer : JsonSerializer {
    private val jackson = jacksonObjectMapper()
    private val jacksonScalar = jacksonObjectMapper()

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

    override fun <T> resolve(type: Class<T>, value: Map<String, Any>): T {
        return jacksonScalar.convertValue(value, type)
    }

    override fun registerResolverType(type: Class<*>, coercing: Coercing<*, *>) {
        val module = SimpleModule()
        module.addDeserializer(type, ScalarCoercingDeserializer(coercing))
        module.addSerializer(type, ScalarCoercingSerializer(coercing))
        jacksonScalar.registerModule(module)
    }
}