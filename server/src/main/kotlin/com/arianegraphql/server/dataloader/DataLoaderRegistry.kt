package com.arianegraphql.server.dataloader

import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry

/**
 * Builder for creating a DataLoaderRegistry with a DSL.
 */
@DslMarker
annotation class DataLoaderRegistryDslMarker

@DataLoaderRegistryDslMarker
class DataLoaderRegistryBuilder {
    private val registry = DataLoaderRegistry()

    /**
     * Register a DataLoader with the given name.
     *
     * Note: since java-dataloader 5.0, a DataLoader that was created with a name
     * (e.g. via `DataLoaderFactory.newDataLoader("name", ...)`) must be registered
     * under that same name, otherwise registration throws.
     */
    fun register(name: String, dataLoader: DataLoader<*, *>) {
        registry.register(name, dataLoader)
    }

    fun build(): DataLoaderRegistry = registry
}

/**
 * DSL function to create a DataLoaderRegistry.
 */
fun dataLoaderRegistry(block: DataLoaderRegistryBuilder.() -> Unit): DataLoaderRegistry {
    return DataLoaderRegistryBuilder().apply(block).build()
}
