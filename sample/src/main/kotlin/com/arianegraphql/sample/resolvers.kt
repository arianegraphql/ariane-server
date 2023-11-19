package com.arianegraphql.sample

import com.arianegraphql.dsl.resolvers
import com.arianegraphql.sample.model.MutationWithNullableScalar
import com.arianegraphql.sample.model.MutationWithScalarArray
import com.arianegraphql.sample.model.ProductType
import com.arianegraphql.sample.model.ResultObject
import com.example.resolver.*

val myResolvers = resolvers {
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