package com.arianegraphql.ktx

import com.arianegraphql.server.dataloader.BatchDataLoaderFactory
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class RuntimeWiringBuilder {
    val runtimeWiringBuilder: RuntimeWiring.Builder = newRuntimeWiring()
    val registeredScalarTypes: MutableMap<Class<*>, Coercing<*, *>> = mutableMapOf()
    val batchLoaderFactories: MutableMap<String, BatchDataLoaderFactory> = mutableMapOf()

    fun resolvers(builder: RootResolverBuilder.() -> Unit) {
        val rootBuilder = RootResolverBuilder().apply(builder)
        addResolvers(rootBuilder.build())
        batchLoaderFactories += rootBuilder.batchLoaderFactories
    }

    fun addResolvers(resolvers: List<TypeRuntimeWiring.Builder>) = resolvers.forEach(runtimeWiringBuilder::type)

    inline fun <reified T> scalar(scalar: GraphQLScalarType) {
        runtimeWiringBuilder.scalar(scalar)
        registeredScalarTypes[T::class.java] = scalar.coercing
    }

    inline fun <reified I, O> scalar(name: String, description: String = "", coercing: Coercing<I, O>) = scalar<I>(
        GraphQLScalarType
            .newScalar()
            .name(name)
            .description(description)
            .coercing(coercing)
            .build()
    )

    inline fun <reified I, O> scalar(name: String, builder: ScalarBuilder<I?, O?>.() -> Unit) =
        scalar<I>(ScalarBuilder<I?, O?>().apply(builder).build(name))
}

fun resolvers(builder: RootResolverBuilder.() -> Unit): List<TypeRuntimeWiring.Builder> =
    RootResolverBuilder().apply(builder).build()