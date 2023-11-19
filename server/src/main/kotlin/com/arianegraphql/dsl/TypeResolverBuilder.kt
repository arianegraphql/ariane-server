package com.arianegraphql.dsl

import graphql.schema.DataFetcher
import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class TypeResolverBuilder<S> {

    val typeResolvers = mutableMapOf<String, DataFetcher<*>>()

    @JvmName("resolveWithType")
    inline fun <reified A> resolve(field: String, resolver: Resolver<S, A>) {
        typeResolvers[field] = resolver.toDataFetcher()
    }

    @JvmName("resolveWithType")
    inline fun <reified A> resolve(field: String, noinline resolver: suspend ResolverParameters<S>.(A) -> Any?) {
        typeResolvers[field] = FunctionalResolver(resolver).toDataFetcher()
    }

    fun resolve(field: String, resolver: Resolver<S, Map<String, Any?>>) {
        typeResolvers[field] = resolver.toDataFetcher()
    }

    fun resolve(field: String, resolver: suspend ResolverParameters<S>.(Map<String, Any?>) -> Any?) {
        typeResolvers[field] = FunctionalResolver(resolver).toDataFetcher()
    }

    fun build(name: String): TypeRuntimeWiring.Builder = TypeRuntimeWiring.newTypeWiring(name).let {
        typeResolvers.forEach { (name, dataFetcher) ->
            it.dataFetcher(name, dataFetcher)
        }
        it
    }
}