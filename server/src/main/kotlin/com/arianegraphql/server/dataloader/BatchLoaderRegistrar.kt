package com.arianegraphql.server.dataloader

import com.arianegraphql.server.request.IncomingRequest
import graphql.GraphQLContext
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry

/**
 * Creates a request-scoped [DataLoader] for a batch resolver. DataLoaders cache per
 * instance, so a fresh one is created for every request from the request's context.
 */
typealias BatchDataLoaderFactory = (GraphQLContext) -> DataLoader<*, *>

/**
 * Registers the DataLoaders of all batch resolvers declared through the DSL into a
 * per-request [DataLoaderRegistry]. Loader names are deterministic
 * (`TypeName.fieldName`), which also makes them traceable in graphql-java's
 * per-loader statistics.
 */
class BatchLoaderRegistrar(
    private val factories: Map<String, BatchDataLoaderFactory>,
) {

    fun isEmpty(): Boolean = factories.isEmpty()

    fun registerInto(registry: DataLoaderRegistry, context: GraphQLContext): DataLoaderRegistry {
        factories.forEach { (name, factory) ->
            registry.register(name, factory(context))
        }
        return registry
    }

    companion object {
        val EMPTY = BatchLoaderRegistrar(emptyMap())
    }
}

/**
 * Builds the effective registry for one request: the user-provided registry (if any)
 * plus the DataLoaders of all DSL-declared batch resolvers. Returns null when there is
 * nothing to register, preserving executions without any registry.
 */
internal suspend fun buildRequestDataLoaderRegistry(
    provider: DataLoaderRegistryProvider?,
    registrar: BatchLoaderRegistrar,
    request: IncomingRequest,
    context: GraphQLContext,
): DataLoaderRegistry? {
    val userRegistry = provider?.provide(request, context)
    if (userRegistry == null && registrar.isEmpty()) return null

    return registrar.registerInto(userRegistry ?: DataLoaderRegistry(), context)
}
