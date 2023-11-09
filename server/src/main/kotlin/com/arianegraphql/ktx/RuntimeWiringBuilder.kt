package com.arianegraphql.ktx

import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class RuntimeWiringBuilder: EnumProviderBuilder() {
    private val runtimeWiringBuilder: RuntimeWiring.Builder = newRuntimeWiring()

    fun resolvers(builder: RootResolverBuilder.() -> Unit) = addResolvers(RootResolverBuilder().apply(builder).build())

    fun addResolvers(resolvers: List<TypeRuntimeWiring.Builder> ) = resolvers.forEach {
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

    fun build(): RuntimeWiring {
        enumProviders.values.forEach {
            runtimeWiringBuilder.type(it)
        }
        return runtimeWiringBuilder.build()
    }
}

fun resolvers(builder: RootResolverBuilder.() -> Unit): List<TypeRuntimeWiring.Builder>
= RootResolverBuilder().apply(builder).build()