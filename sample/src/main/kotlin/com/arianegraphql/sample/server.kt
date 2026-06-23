package com.arianegraphql.sample

import com.arianegraphql.ktx.loadSchema
import com.arianegraphql.sample.data.Post
import com.arianegraphql.sample.data.PostRepository
import com.arianegraphql.sample.data.UserRepository
import com.arianegraphql.sample.gql.resolver.*
import com.arianegraphql.sample.gql.type.*
import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.listener.ServerListener
import graphql.language.StringValue
import java.time.Instant

val myServer = arianeServer {
    schema = loadSchema("schema.graphqls")
    port = 3000

    cors = {
        anyHost()
    }

    serverListener = object : ServerListener {

        override fun onStart(host: String, port: Int, path: String) {
            println("Server ready 🚀")
            println("Listening at http://localhost:$port$path")
            println("\n=== Simplified Batch Resolver Demo ===")
            println("Try this query to see automatic batching in action:")
            println("""
                query { 
                  posts {
                    id
                    title
                    author {
                      id
                      name
                      email
                    }
                  }
                }
            """.trimIndent())
            println("\nNotice how batch resolvers are registered inline - no manual DataLoader setup needed!\n")
        }
    }

    scalar("Date") {

        serialize { result -> (result as? Instant)?.toString() }

        parseValue { input -> (input as? String)?.let(Instant::parse) }

        parseLiteral { input -> (input as? StringValue)?.value?.let(Instant::parse) }
    }

    resolvers {
        // Query resolvers
        Query {

            posts { _ ->
                println("\n[QUERY] Fetching all posts...")
                PostRepository.findAll().map {
                    com.arianegraphql.sample.gql.type.Post(
                        id = it.id,
                        title = it.title,
                        content = it.content,
                        authorId = it.authorId
                    )
                }
            }

            post { args ->
                println("\n[QUERY] Fetching post by ID: ${args.id}")
                PostRepository.findById(args.id)?.let {
                    com.arianegraphql.sample.gql.type.Post(
                        id = it.id,
                        title = it.title,
                        content = it.content,
                        authorId = it.authorId
                    )
                }
            }
        }

        Post {

            author.batch(
                key = { _, post -> post.authorId!! }
            ) { keys ->
                println("\n[BATCH RESOLVER] Fetching ${keys.size} users in a single batch:")
                println("[BATCH RESOLVER] User IDs: ${keys.joinToString(", ")}")

                val users = UserRepository.findAllByIds(keys.toList()).filterNotNull()

                println("[BATCH RESOLVER] Successfully fetched ${users.size} users\n")
                users.associate { it.id to User(it.id, it.name, it.email) }
            }
        }

        Mutation {

            mutationWithEnum {
                println("Received ${it.input}")

                ProductType.PRODUCT_A
            }

            mutationWithEnumArray {
                println("Received ${it.input}")

                ProductType.entries
            }

            mutationWithNullableEnum {
                println("Received ${it.input}")

                null
            }

            mutationWithInputObject {
                println("Received ${it.input}")

                ResultObject("a", "b", "c", ProductType.UNKNOWN)
            }

            mutationWithInputObjectArray {
                println("Received ${it.input}")

                listOf(ResultObject("a", "b", "c", ProductType.UNKNOWN))
            }

            mutationWithValues { (string) ->
                println("Received $string")

                ResultObject("a", "b", "c", ProductType.UNKNOWN)
            }

            mutationWithNullableValues {
                val string = it.string
                println("Received $string")

                null
            }

            mutationWithInputObjectArray {
                println("Received ${it.input}")

                listOf(ResultObject("a", "b", "c", ProductType.UNKNOWN))
            }

            mutationWithScalar { (input) ->
                println("Received $input")

                ResultObject("a", "b", "c", ProductType.UNKNOWN)
            }
        }
    }
}
