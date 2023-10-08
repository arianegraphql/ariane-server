package com.arianegraphql.ktx

import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class TypeResolverBuilder<S> {

    internal val typeResolver = mutableMapOf<String, Resolver<*>>()

    fun resolve(field: String, resolver: Resolver<S>) {
        typeResolver[field] = resolver
    }

    fun resolve(field: String, resolver: suspend ResolverParameters<S>.() -> Any?) {
        typeResolver[field] = FunctionalResolver(resolver)
    }

    internal fun build(name: String): TypeRuntimeWiring.Builder = TypeRuntimeWiring.newTypeWiring(name).let {
        typeResolver.forEach { (name, resolver) ->
            it.dataFetcher(name, resolver.toDataFetcher())
        }
        it
    }
}