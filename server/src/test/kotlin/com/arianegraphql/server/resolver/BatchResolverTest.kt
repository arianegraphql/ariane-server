package com.arianegraphql.server.resolver

import com.arianegraphql.ktx.ArgumentTypeResolver
import com.arianegraphql.ktx.FieldRef
import com.arianegraphql.ktx.RuntimeWiringBuilder
import com.arianegraphql.ktx.argumentTypeResolver
import com.arianegraphql.ktx.makeExecutableSchema
import com.arianegraphql.server.dataloader.BatchLoaderRegistrar
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.GraphQLContext
import graphql.schema.Coercing
import org.dataloader.DataLoaderRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

internal class BatchResolverTest {

    data class TestPost(val id: String, val authorId: String)
    data class TestUser(val id: String, val name: String)

    private val schema = """
        type Query { posts: [Post!]! }
        type Post { id: ID! authorId: ID! author: User }
        type User { id: ID! name: String! }
    """.trimIndent()

    private val posts = listOf(
        TestPost("p1", "u1"),
        TestPost("p2", "u2"),
        TestPost("p3", "u1"),
        TestPost("p4", "missing"),
    )

    private val unitArguments = object : ArgumentTypeResolver {
        @Suppress("UNCHECKED_CAST")
        override fun <T> resolve(type: Class<T>, value: Map<String, Any>, graphQLContext: GraphQLContext): T = Unit as T

        override fun registerResolverType(type: Class<*>, coercing: Coercing<*, *>) = Unit
    }

    @Test
    fun `batches and deduplicates keys in a single loader call, missing keys resolve to null`() {
        val batchCalls = AtomicInteger()
        val batchedKeys = mutableListOf<Set<String>>()

        val wiring = RuntimeWiringBuilder().apply {
            resolvers {
                Query {
                    resolve<Unit, List<TestPost>>("posts") { posts }
                }

                type<TestPost>("Post") {
                    resolveBatch<Unit, String, TestUser?>(
                        field = "author",
                        keyExtractor = { _, post -> post.authorId },
                    ) { keys ->
                        batchCalls.incrementAndGet()
                        batchedKeys += keys

                        // Each key carries the source of its first field occurrence
                        keys.forEach { key ->
                            assertEquals(key, itemFor(key)?.source?.authorId)
                        }

                        keys.filter { it != "missing" }.associateWith { TestUser(it, "name-$it") }
                    }
                }
            }
        }

        val graphQL = GraphQL.newGraphQL(makeExecutableSchema(schema, wiring.runtimeWiringBuilder.build())).build()

        val context = GraphQLContext.newContext().argumentTypeResolver(unitArguments).build()
        val registry = BatchLoaderRegistrar(wiring.batchLoaderFactories).registerInto(DataLoaderRegistry(), context)

        val result = graphQL.execute(
            ExecutionInput.newExecutionInput("{ posts { id author { id name } } }")
                .graphQLContext { it.of(context) }
                .dataLoaderRegistry(registry)
                .build()
        )

        assertTrue(result.errors.isEmpty(), result.errors.toString())

        val data = result.getData<Map<String, List<Map<String, Map<String, String>?>>>>()!!
        val authors = data.getValue("posts").map { it["author"] }

        assertEquals("name-u1", authors[0]?.get("name"))
        assertEquals("name-u2", authors[1]?.get("name"))
        assertEquals("name-u1", authors[2]?.get("name"))
        assertNull(authors[3])

        assertEquals(1, batchCalls.get())
        assertEquals(setOf("u1", "u2", "missing"), batchedKeys.single())
    }

    @Test
    fun `data loader names are deterministic`() {
        val wiring = RuntimeWiringBuilder().apply {
            resolvers {
                type<TestPost>("Post") {
                    resolveBatch<Unit, String, TestUser?>(
                        field = "author",
                        keyExtractor = { _, post -> post.authorId },
                    ) { keys -> keys.associateWith { TestUser(it, it) } }
                }
            }
        }

        assertEquals(setOf("Post.author"), wiring.batchLoaderFactories.keys)
    }

    @Test
    fun `field refs register plain and batch resolvers`() {
        val wiring = RuntimeWiringBuilder().apply {
            resolvers {
                type<TestPost>("Post") {
                    val id = FieldRef<TestPost, Unit, String>(this, "id", Unit::class.java)
                    val author = FieldRef<TestPost, Unit, TestUser?>(this, "author", Unit::class.java)

                    id { source.id }
                    author.batch(key = { _, post -> post.authorId }) { keys ->
                        keys.associateWith { TestUser(it, it) }
                    }
                }
            }
        }

        assertEquals(setOf("Post.author"), wiring.batchLoaderFactories.keys)
    }
}
