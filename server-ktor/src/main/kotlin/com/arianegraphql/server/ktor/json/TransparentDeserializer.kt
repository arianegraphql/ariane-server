package com.arianegraphql.server.ktor.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import graphql.GraphQLContext
import graphql.schema.Coercing
import java.util.Locale

class ScalarCoercingSerializer<T>(
    private val coercing: Coercing<*, *>,
    private val graphQLContext: () -> GraphQLContext,
) : JsonSerializer<T>() {

    override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeObject(coercing.serialize(value as Any, graphQLContext(), Locale.getDefault()))
    }
}

class ScalarCoercingDeserializer<T>(
    private val coercing: Coercing<*, *>,
    private val graphQLContext: () -> GraphQLContext,
) : JsonDeserializer<T>() {

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T? {
        val obj = p.readValueAs(Any::class.java)
        return coercing.parseValue(obj, graphQLContext(), Locale.getDefault()) as? T
    }
}