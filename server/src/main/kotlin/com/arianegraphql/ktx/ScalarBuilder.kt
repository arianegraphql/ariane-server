package com.arianegraphql.ktx

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType

@GraphQLSchemaDslMarker
class ScalarBuilder<I, O> {

    var description: String? = null

    lateinit var serialize: (dataFetcherResult: Any) -> O
    lateinit var parseValue: (input: Any) -> I
    lateinit var parseLiteral: (input: Any) -> I

    fun serialize(lambda: (dataFetcherResult: Any) -> O){
        this.serialize = lambda
    }

    fun parseValue(lambda: (input: Any) -> I){
        this.parseValue = lambda
    }

    fun parseLiteral(lambda: (input: Any) -> I){
        this.parseLiteral = lambda
    }

    fun build(name: String) = GraphQLScalarType.newScalar()
        .name(name)
        .description(description)
        .coercing(object : Coercing<I, O> {

            override fun serialize(dataFetcherResult: Any) = this@ScalarBuilder.serialize(dataFetcherResult)

            override fun parseValue(input: Any) = this@ScalarBuilder.parseValue(input)

            override fun parseLiteral(input: Any) = this@ScalarBuilder.parseLiteral(input)
        }).build()
}