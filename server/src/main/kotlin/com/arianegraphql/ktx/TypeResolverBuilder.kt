package com.arianegraphql.ktx

import com.arianegraphql.server.dataloader.BatchDataLoaderFactory
import com.arianegraphql.server.resolver.*
import graphql.schema.DataFetcher
import graphql.schema.idl.TypeRuntimeWiring

@GraphQLSchemaDslMarker
open class TypeResolverBuilder<S> {

    val typeResolvers = mutableMapOf<String, DataFetcher<*>>()

    @PublishedApi
    internal val pendingBatchResolvers = mutableMapOf<String, PendingBatchResolver<S, *, *, *>>()

    @JvmName("resolveWithType")
    inline fun <reified A, R: Any?> resolve(field: String, resolver: Resolver<S, A, R>) {
        resolve(field, A::class.java, resolver)
    }

    @JvmName("resolveWithType")
    inline fun <reified A, R: Any?> resolve(field: String, noinline resolver: suspend ResolverContext<S>.(A) -> R) {
        resolve(field, A::class.java, FunctionalResolver(resolver))
    }

    fun <A, R: Any?> resolve(field: String, argumentType: Class<A>, resolver: Resolver<S, A, R>) {
        typeResolvers[field] = resolver.toDataFetcher(argumentType)
    }

    /**
     * Register a batch resolver for this field, solving N+1 queries through a DataLoader.
     *
     * Keys extracted by [keyExtractor] from every occurrence of the field are collected,
     * deduplicated and handed to [loader] in a single call per execution level. The
     * DataLoader is created and registered automatically for each request; missing keys
     * resolve to null.
     *
     * Inside [loader], [BatchResolverEnvironment] exposes the request's GraphQLContext
     * and the source/arguments of each key (first occurrence per key).
     *
     * @param field The GraphQL field name
     * @param keyExtractor Extracts the batching key from the field's arguments and source
     * @param loader Fetches all keys at once, returning a map of key to result
     *
     * @example
     * ```
     * type<Post>("Post") {
     *     resolveBatch<Unit, String, User?>(
     *         field = "author",
     *         keyExtractor = { _, post -> post.authorId }
     *     ) { keys ->
     *         userRepository.findAllByIds(keys).associateBy { it.id }
     *     }
     * }
     * ```
     */
    inline fun <reified A, K : Any, R: Any?> resolveBatch(
        field: String,
        noinline keyExtractor: (A, S) -> K,
        noinline loader: suspend BatchResolverEnvironment<S, A, K>.(keys: Set<K>) -> Map<K, R>
    ) {
        resolveBatch(field, A::class.java, keyExtractor, loader)
    }

    fun <A, K : Any, R: Any?> resolveBatch(
        field: String,
        argumentType: Class<A>,
        keyExtractor: (A, S) -> K,
        loader: suspend BatchResolverEnvironment<S, A, K>.(keys: Set<K>) -> Map<K, R>
    ) {
        pendingBatchResolvers[field] = PendingBatchResolver(argumentType, keyExtractor, loader)
    }

    /**
     * Materializes the DataLoader factories of the batch resolvers registered on this
     * type, keyed by their definitive DataLoader name (`TypeName.fieldName`).
     */
    internal fun dataLoaderFactories(typeName: String): Map<String, BatchDataLoaderFactory> =
        pendingBatchResolvers.entries.associate { (field, pending) ->
            val dataLoaderName = "$typeName.$field"
            dataLoaderName to pending.dataLoaderFactory(dataLoaderName)
        }

    fun build(name: String): TypeRuntimeWiring.Builder = TypeRuntimeWiring.newTypeWiring(name).let {
        pendingBatchResolvers.forEach { (field, pending) ->
            typeResolvers[field] = pending.dataFetcher("$name.$field")
        }
        typeResolvers.forEach { (field, dataFetcher) ->
            it.dataFetcher(field, dataFetcher)
        }
        it
    }
}
