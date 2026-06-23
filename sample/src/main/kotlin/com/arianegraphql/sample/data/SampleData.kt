package com.arianegraphql.sample.data

/**
 * Sample data classes for demonstrating batch resolvers
 */

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val authorId: String
)

/**
 * In-memory repository simulating a database
 * Logs queries to demonstrate batching benefits
 */
object UserRepository {
    private val users = listOf(
        User("1", "Alice Johnson", "alice@example.com"),
        User("2", "Bob Smith", "bob@example.com"),
        User("3", "Carol Williams", "carol@example.com"),
        User("4", "David Brown", "david@example.com"),
        User("5", "Eve Davis", "eve@example.com")
    )

    /**
     * Simulates a single user lookup - would cause N+1 problem if used directly
     */
    fun findById(id: String): User? {
        println("  [DB] UserRepository.findById($id)")
        Thread.sleep(10) // Simulate database latency
        return users.find { it.id == id }
    }

    /**
     * Batched lookup - demonstrates the benefit of batch resolvers
     */
    fun findAllByIds(ids: List<String>): List<User?> {
        println("  [DB] UserRepository.findAllByIds(${ids.joinToString(", ")})")
        println("  [DB] ^ Single query fetching ${ids.size} users!")
        Thread.sleep(10) // Simulate database latency

        // Return users in the same order as requested IDs
        return ids.map { id -> users.find { it.id == id } }
    }
}

object PostRepository {
    private val posts = listOf(
        Post("101", "Introduction to GraphQL", "GraphQL is a query language...", "1"),
        Post("102", "Understanding Batching", "Batching helps solve N+1 problems...", "2"),
        Post("103", "Kotlin Coroutines", "Coroutines make async code easy...", "1"),
        Post("104", "Server-Side Development", "Building robust backends...", "3"),
        Post("105", "DataLoader Pattern", "DataLoader batches and caches...", "2"),
        Post("106", "GraphQL Best Practices", "Follow these patterns...", "4"),
        Post("107", "Testing GraphQL APIs", "Comprehensive testing strategies...", "1"),
        Post("108", "Schema Design", "Design your schema carefully...", "5")
    )

    fun findAll(): List<Post> {
        println("  [DB] PostRepository.findAll()")
        return posts
    }

    fun findById(id: String): Post? {
        println("  [DB] PostRepository.findById($id)")
        return posts.find { it.id == id }
    }
}
