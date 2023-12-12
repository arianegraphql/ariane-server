package com.arianegraphql.sample

import com.arianegraphql.ktx.loadSchema
import com.arianegraphql.sample.model.*
import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.listener.ServerListener
import com.example.resolver.*
import graphql.language.StringValue
import java.time.Instant

val myServer = arianeServer {
    schema = loadSchema("schema.graphql")
    port = 3000

    cors = {
        anyHost()
    }

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

            mutationWithEnum {
                println("Received ${it.input}")

                ProductType.PRODUCT_A
            }

            mutationWithEnumArray {
                println("Received ${it.input}")

                ProductType.values()
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

            mutationWithInputObjectArray { (input) ->
                println("Received $input")

                null
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
}
