package com.arianegraphql.ktx

import graphql.schema.DataFetcher
import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class TypeResolverBuilder<S> {

    val typeResolvers = mutableMapOf<String, DataFetcher<*>>()

    @JvmName("resolveWithType")
    inline fun <reified A, R: Any?> resolve(field: String, resolver: Resolver<S, A, R>) {
        typeResolvers[field] = resolver.toDataFetcher()
    }

    @JvmName("resolveWithType")
    inline fun <reified A, R: Any?> resolve(field: String, noinline resolver: suspend ResolverParameters<S>.(A) -> R) {
        typeResolvers[field] = FunctionalResolver(resolver).toDataFetcher()
    }

    fun build(name: String): TypeRuntimeWiring.Builder = TypeRuntimeWiring.newTypeWiring(name).let {
        typeResolvers.forEach { (name, dataFetcher) ->
            it.dataFetcher(name, dataFetcher)
        }
        it
    }
}