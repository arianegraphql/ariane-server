package com.arianegraphql.ktx

import graphql.GraphQLContext
import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class TypeResolverBuilder {

    internal val typeResolver = mutableMapOf<String, Resolver<*>>()

    fun <S> resolve(field: String, resolver: Resolver<S>) {
        typeResolver[field] = resolver
    }

    fun <S> resolve(field: String, resolver: suspend (arguments: Argument, source: S, context: GraphQLContext, info: Info) -> Any?) {
        typeResolver[field] = FunctionalResolver(resolver)
    }

    internal fun build(name: String): TypeRuntimeWiring.Builder = TypeRuntimeWiring.newTypeWiring(name).let {
        typeResolver.forEach { (name, resolver) ->
            it.dataFetcher(name, resolver.toDataFetcher())
        }
        it
    }
}