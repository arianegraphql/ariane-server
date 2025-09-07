package com.arianegraphql.sample

import com.arianegraphql.ktx.resolvers
import com.arianegraphql.sample.gql.resolver.*
import com.arianegraphql.sample.gql.type.*

val myResolvers = resolvers {
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