package com.arianegraphql.server.dataloader

import com.arianegraphql.server.request.IncomingRequest
import graphql.GraphQLContext
import org.dataloader.DataLoaderRegistry

/**
 * Provider for creating DataLoaderRegistry instances.
 *
 * DataLoaderRegistry should be created per-request to avoid caching data
 * across different users or requests.
 */
interface DataLoaderRegistryProvider {
    /**
     * Create a new DataLoaderRegistry for the given request and context.
     *
     * @param request The incoming request
     * @param context The GraphQL context
     * @return A new DataLoaderRegistry instance
     */
    suspend fun provide(request: IncomingRequest, context: GraphQLContext): DataLoaderRegistry
}

/**
 * Functional implementation of DataLoaderRegistryProvider.
 */
class FunctionalDataLoaderRegistryProvider(
    private val provider: suspend (IncomingRequest, GraphQLContext) -> DataLoaderRegistry
) : DataLoaderRegistryProvider {
    override suspend fun provide(request: IncomingRequest, context: GraphQLContext): DataLoaderRegistry {
        return provider(request, context)
    }
}

/**
 * A simple DataLoaderRegistryProvider that always returns the same registry.
 *
 * WARNING: This is only suitable for testing or single-user scenarios.
 * For production use, create a new registry per request.
 */
class StaticDataLoaderRegistryProvider(
    private val registry: DataLoaderRegistry
) : DataLoaderRegistryProvider {
    override suspend fun provide(request: IncomingRequest, context: GraphQLContext): DataLoaderRegistry {
        return registry
    }
}
