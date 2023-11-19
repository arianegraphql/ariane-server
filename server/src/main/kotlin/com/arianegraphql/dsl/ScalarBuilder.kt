package com.arianegraphql.dsl

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import java.util.*

@GraphQLSchemaDslMarker
class ScalarBuilder<I, O> {
    private lateinit var serializeFunction: ScalarSerializerEnvironment.(dataFetcherResult: Any) -> O?

    private lateinit var parseValueFunction: ScalarValueParserEnvironment.(input: Any) -> I?

    private lateinit var parseLiteralFunction: ScalarLiteralParserEnvironment.(input: Value<*>) -> I?

    fun serialize(lambda: ScalarSerializerEnvironment.(dataFetcherResult: Any) -> O?) {
        this.serializeFunction = lambda
    }

    fun parseValue(lambda: ScalarValueParserEnvironment.(input: Any) -> I?) {
        this.parseValueFunction = lambda
    }

    fun parseLiteral(
        lambda: ScalarLiteralParserEnvironment.(input: Value<*>) -> I?
    ) {
        this.parseLiteralFunction = lambda
    }

    fun build(name: String): GraphQLScalarType = GraphQLScalarType.newScalar()
        .name(name)
        .coercing(object : Coercing<I, O> {

            override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): O? =
                with(ScalarSerializerEnvironment(graphQLContext, locale)) {
                    serializeFunction(dataFetcherResult)
                }

            override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): I? =
                with(ScalarValueParserEnvironment(graphQLContext, locale)) {
                    parseValueFunction(input)
                }

            override fun parseLiteral(
                input: Value<*>,
                variables: CoercedVariables,
                graphQLContext: GraphQLContext,
                locale: Locale
            ): I? =
                with(ScalarLiteralParserEnvironment(variables, graphQLContext, locale)) {
                    parseLiteralFunction(input)
                }
        }).build()
}

data class ScalarSerializerEnvironment(
    val graphQLContext: GraphQLContext,
    val locale: Locale,
)

data class ScalarValueParserEnvironment(
    val graphQLContext: GraphQLContext,
    val locale: Locale,
)

data class ScalarLiteralParserEnvironment(
    val variables: CoercedVariables,
    val graphQLContext: GraphQLContext,
    val locale: Locale
)

