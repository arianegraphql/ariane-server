package com.arianegraphql.sample

import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.ktor.launch
import com.arianegraphql.ktx.loadSchema
import com.arianegraphql.sample.model.*
import com.arianegraphql.server.listener.ServerListener
import graphql.language.StringValue
import java.time.Instant


fun main() {
    arianeServer {
        schema = loadSchema("schema.graphql")
        port = 3000

        serverListener = object : ServerListener {

            override fun onStart(host: String, port: Int, path: String) {
                println("Server ready 🚀")
                println("Listening at http://localhost:$port$path")
            }
        }

        scalar("Date") {
            serialize { result -> (result as? Instant)?.toString() }

            parseValue { input -> (input as? String)?.let(Instant::parse) }

            parseLiteral { input -> (input as? StringValue)?.value?.let(Instant::parse) }
        }

        resolvers {
            Mutation {

                resolve("mutationWithEnum") { args: MutationWithEnumArguments ->
                    println("Received ${args.input}")

                    ProductType.PRODUCT_A
                }

                resolve<MutationWithEnumArrayArguments>("mutationWithEnumArray") { (input) ->
                    println("Received $input")

                    ProductType.values()
                }

                resolve<MutationWithNullableEnumArguments>("mutationWithNullableEnum") { (input) ->
                    println("Received $input")

                    null
                }

                resolve("mutationWithInputObject") { args: MutationWithInputObjectArguments ->
                    println("Received ${args.input}")

                    ResultObject("a", "b", "c", ProductType.UNKNOWN)
                }

                resolve<MutationWithInputObjectArrayArguments>("mutationWithInputObjectArray") { (input) ->
                    println("Received $input")

                    listOf(ResultObject("a", "b", "c", ProductType.UNKNOWN))
                }

                resolve<MutationWithNullableInputObjectArguments>("mutationWithNullableInputObject") { (input) ->
                    println("Received $input")

                    null
                }

                resolve<MutationWithValuesArguments>("mutationWithValues") { (string) ->
                    println("Received $string")

                    ResultObject("a", "b", "c", ProductType.UNKNOWN)
                }

                resolve("mutationWithNullableValues") {
                    val string = it["string"]
                    println("Received $string")

                    null
                }

                resolve<MutationWithScalar>("mutationWithScalar") { (input) ->
                    println("Received $input")

                    ResultObject("a", "b", "c", ProductType.UNKNOWN)
                }

                resolve<MutationWithScalarArray>("mutationWithScalarArray") { (input) ->
                    println("Received $input")

                    listOf(ResultObject("a", "b", "c", ProductType.UNKNOWN))
                }

                resolve<MutationWithNullableScalar>("mutationWithNullableScalar") { (input) ->
                    println("Received $input")

                    null
                }
            }

        }
    }.launch()
}