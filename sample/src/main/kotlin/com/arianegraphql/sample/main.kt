package com.arianegraphql.sample

import com.arianegraphql.server.ktor.dsl.arianeServer
import com.arianegraphql.server.ktor.launch
import com.arianegraphql.ktx.loadSchema
import com.arianegraphql.server.listener.ServerListener

data class MyUserInput(val id: String, val name: String?, val birthday: String?, val productType: ProductType)
enum class ProductType { PRODUCT_A, PRODUCT_B, UNKNOWN }

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

        enum<ProductType>()

        resolvers {
            Mutation {
                resolve("mutationWithUser") {
                    val userId: String = arguments["userId"]
                    val userName: String? = arguments["userName"]
                    val userBirthday: String? = arguments["userBirthday"]
                    val productType: ProductType = arguments["userProduct"]
                    println("Hello World!")
                }
            }

        }
    }.launch()
}