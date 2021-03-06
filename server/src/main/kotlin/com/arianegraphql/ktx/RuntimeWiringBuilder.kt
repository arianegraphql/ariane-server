package com.arianegraphql.ktx

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring

@GraphQLSchemaDslMarker
open class RuntimeWiringBuilder {
    private val runtimeWiringBuilder: RuntimeWiring.Builder = newRuntimeWiring()

    fun resolvers(builder: RootResolverBuilder.() -> Unit) = RootResolverBuilder().apply(builder).build().forEach {
        runtimeWiringBuilder.type(it)
    }

    fun scalar(scalar: GraphQLScalarType) {
        runtimeWiringBuilder.scalar(scalar)
    }

    fun <I, O> scalar(name: String, description: String = "", coercing: Coercing<I, O>) = scalar(
        GraphQLScalarType
            .newScalar()
            .name(name)
            .description(description)
            .coercing(coercing)
            .build()
    )

    fun <I, O> scalar(name: String, builder: ScalarBuilder<I?, O?>.() -> Unit) =
        scalar(ScalarBuilder<I?, O?>().apply(builder).build(name))

    fun build() = runtimeWiringBuilder.build()
}

fun resolvers(builder: RootResolverBuilder.() -> Unit): RuntimeWiring {
    val runtimeWiringBuilder: RuntimeWiring.Builder = newRuntimeWiring()

    RootResolverBuilder().apply(builder).build().forEach {
        runtimeWiringBuilder.type(it)
    }

    return runtimeWiringBuilder.build()
}