package com.arianegraphql.server.resolver

import com.arianegraphql.ktx.argumentTypeResolver
import com.arianegraphql.server.dataloader.BatchDataLoaderFactory
import graphql.GraphQLContext
import graphql.schema.DataFetcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.dataloader.MappedBatchLoaderWithContext
import java.util.concurrent.CompletableFuture

/**
 * The source object and arguments of one field occurrence that requested a batched key.
 */
data class BatchItem<S, A>(
    val source: S,
    val arguments: A,
)

/**
 * Environment available to a batch loader while it resolves a set of keys.
 *
 * @property context The GraphQL context of the request being executed.
 * @property items The [BatchItem] (source and arguments) associated with each key.
 *   Keys are deduplicated before batching, so when several field occurrences map to
 *   the same key, only the first occurrence's item is retained.
 */
class BatchResolverEnvironment<S, A, K : Any> internal constructor(
    val context: GraphQLContext,
    val items: Map<K, BatchItem<S, A>>,
) {
    fun itemFor(key: K): BatchItem<S, A>? = items[key]
}

private val batchScope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("ariane-batch-loader"))

/**
 * A batch resolver registration captured by the DSL before the GraphQL type name is
 * known. The definitive DataLoader name (`TypeName.fieldName`) is assigned when the
 * type wiring is built, at which point this materializes into a [DataFetcher] for the
 * field and a per-request [BatchDataLoaderFactory] for the matching DataLoader.
 */
@PublishedApi
internal class PendingBatchResolver<S, A, K : Any, R>(
    private val argumentType: Class<A>,
    private val keyExtractor: (A, S) -> K,
    private val loader: suspend BatchResolverEnvironment<S, A, K>.(Set<K>) -> Map<K, R>,
) {

    fun dataFetcher(dataLoaderName: String): DataFetcher<CompletableFuture<R>> = DataFetcher { env ->
        val arguments = env.graphQlContext.argumentTypeResolver.resolve(argumentType, env.arguments, env.graphQlContext)
        val source: S = checkNotNull(env.getSource()) {
            "No source object available for batch-resolved field (DataLoader '$dataLoaderName')"
        }
        val key = keyExtractor(arguments, source)

        val dataLoader = env.getDataLoader<K, R>(dataLoaderName)
            ?: error(
                "DataLoader '$dataLoaderName' is not registered. " +
                    "Batch resolvers require the request to execute with the server-managed DataLoaderRegistry."
            )

        dataLoader.load(key, BatchItem(source, arguments))
    }

    fun dataLoaderFactory(dataLoaderName: String): BatchDataLoaderFactory = { graphQLContext ->
        val batchLoader = MappedBatchLoaderWithContext<K, R> { keys, environment ->
            @Suppress("UNCHECKED_CAST")
            val items = environment.keyContexts as Map<K, BatchItem<S, A>>

            batchScope.future {
                BatchResolverEnvironment(graphQLContext, items).loader(keys)
            }
        }

        DataLoaderFactory.newMappedDataLoader(dataLoaderName, batchLoader, DataLoaderOptions.newOptions().build())
    }
}
