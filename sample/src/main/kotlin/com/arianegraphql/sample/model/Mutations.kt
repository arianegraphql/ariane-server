package com.arianegraphql.sample.model

import java.time.Instant

data class UserObjectInput(
    val userId: String,
    val userName: String?,
    val userBirthday: String?,
    val userProduct: ProductType
)

data class ResultObject(
    val attributeA: String,
    val attributeB: String,
    val attributeC: String,
    val productType: ProductType
)

enum class ProductType { PRODUCT_A, PRODUCT_B, UNKNOWN }

data class MutationWithEnumArguments(val input: ProductType)

data class MutationWithEnumArrayArguments(val input: List<ProductType>)

data class MutationWithNullableEnumArguments(val input: ProductType?)

data class MutationWithInputObjectArguments(val input: UserObjectInput)

data class MutationWithInputObjectArrayArguments(val input: List<UserObjectInput>)

data class MutationWithNullableInputObjectArguments(val input: UserObjectInput?)

data class MutationWithValuesArguments(val string: String, val int: Int, val float: Float, val boolean: Boolean)

data class MutationWithNullableValuesArguments(
    val string: String?,
    val int: Int?,
    val float: Float?,
    val boolean: Boolean?
)

data class MutationWithScalar(val input: Instant)

data class MutationWithScalarArray(val input: List<Instant>)

data class MutationWithNullableScalar(val input: Instant?)