# Batch Resolvers in ArianeGraphQL

Batch resolvers solve the **N+1 query problem** in GraphQL by collecting multiple individual data fetches and executing them as a single batch operation.

## The N+1 Problem

When querying a list of items where each item has related data, naive implementations cause:
- 1 query to fetch the list
- N queries to fetch related data (one per item)

**Example:** Fetching 100 posts with their authors = 101 database queries 😱

## The Solution: Batch Resolvers

Batch resolvers collect all related keys during query execution and fetch them in one batch:
- 1 query to fetch posts
- 1 batched query to fetch all authors

**Result:** 100 posts with authors = 2 database queries 🎉

## Quick Start

Declare a batch resolver on the field. Nothing else to configure — the DataLoader is
created and registered automatically for every request:

```kotlin
val myServer = arianeServer {
    schema = loadSchema("schema.graphqls")

    resolvers {
        type<Post>("Post") {
            resolveBatch<Unit, String, User?>(
                field = "author",
                keyExtractor = { _, post -> post.authorId }
            ) { keys ->
                // One database call for all keys; missing keys resolve to null
                userRepository.findAllByIds(keys).associateBy { it.id }
            }
        }
    }
}
```

With generated code, every field gets a typed reference and the same declaration reads:

```kotlin
Post {
    author.batch(key = { _, post -> post.authorId }) { keys ->
        userRepository.findAllByIds(keys).associateBy { it.id }
    }
}
```

The generated `author` reference also registers plain resolvers (`author { args -> ... }`).
Root mutation fields expose no `batch` — mutations execute serially, so batching them is
inexpressible by design.

## Type Parameters

```kotlin
resolveBatch<A, K, R>(...)
```

- `A` = Arguments type (use `Unit` for fields without arguments)
- `K` = Key type (e.g., `String` for user IDs)
- `R` = Result type (e.g., `User?`)

The source type `S` is inferred from the `type<S>()` context.

## How It Works

1. **Collection**: as GraphQL resolves the query, every occurrence of the field extracts
   its key through `keyExtractor` and enqueues it on a request-scoped DataLoader.
2. **Deduplication**: duplicate keys are collapsed before batching.
3. **Single fetch**: the loader is called once per execution level with the deduplicated
   `Set<K>` and returns a `Map<K, R>`. Keys absent from the map resolve to `null`.
4. **Distribution**: results are distributed back to each field occurrence.

DataLoaders are created **per request** (no caching across users or requests) and named
deterministically `TypeName.fieldName` (e.g. `Post.author`), which makes them traceable
in graphql-java's per-loader statistics. The loader body is a `suspend` function executed
asynchronously — no threads are blocked while batching.

## The Loader Environment

Inside the loader, the receiver is a `BatchResolverEnvironment<S, A, K>`:

```kotlin
resolveBatch<Unit, String, User?>(
    field = "author",
    keyExtractor = { _, post -> post.authorId }
) { keys ->
    context        // the request's GraphQLContext
    items          // Map<K, BatchItem<S, A>>: source and arguments per key
    itemFor(key)   // the BatchItem of one key

    userRepository.findAllByIds(keys).associateBy { it.id }
}
```

Because keys are deduplicated, `items` retains the **first occurrence** per key. If your
loader needs every occurrence, make the occurrence part of the key.

## Custom DataLoaders

Batch resolvers compose with manually managed DataLoaders. Loaders provided through
`dataLoaders { }` are registered alongside the automatic ones:

```kotlin
arianeServer {
    dataLoaders { request, context ->
        dataLoaderRegistry {
            register("myLoader", myCustomDataLoader())
        }
    }
}
```

The registry is created per request, then the framework adds the DataLoaders of all
declared batch resolvers on top of it.

## Subscriptions

Batch resolvers also work for fields resolved during subscription events: each
subscription operation gets its own request-scoped DataLoaderRegistry.

## Performance Impact

| Posts | Without Batching | With Batching | Improvement |
|-------|------------------|---------------|-------------|
| 10    | 11 queries      | 2 queries     | 82% fewer   |
| 100   | 101 queries     | 2 queries     | 98% fewer   |
| 1000  | 1001 queries    | 2 queries     | 99.8% fewer |

## Example: Complete Working Code

See the [sample application](./sample/BATCH_RESOLVER_EXAMPLE.md) for a complete working example with:
- Post and User types
- In-memory repositories
- Console logging to visualize batching
- GraphQL schema and queries to try

## Learn More

- [GraphQL Java Batching Documentation](https://www.graphql-java.com/documentation/batching/)
- [DataLoader Pattern](https://github.com/graphql/dataloader)
- [Sample Application](./sample/BATCH_RESOLVER_EXAMPLE.md)
