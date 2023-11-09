package com.arianegraphql.sample

import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.ktor.launch
import com.arianegraphql.ktx.loadSchema

enum class ProductType { PRODUCT_A, PRODUCT_B, UNKNOWN }

fun main() {
    arianeServer {
        schema = loadSchema("schema.graphql")

        resolvers {
            Query {

                resolve("hello") {
                    "Hello World!"
                }
            }

            Mutation {
                resolve("updateGreeting") {
                    TODO()
                }
            }
        }

    }.launch()
}