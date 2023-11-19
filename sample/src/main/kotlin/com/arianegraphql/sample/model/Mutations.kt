package com.arianegraphql.sample.model

import java.time.Instant

data class ResultObject(
    val attributeA: String,
    val attributeB: String,
    val attributeC: String,
    val productType: ProductType
)

enum class ProductType { PRODUCT_A, PRODUCT_B, UNKNOWN }
data class MutationWithScalar(val input: Instant)

data class MutationWithScalarArray(val input: List<Instant>)

data class MutationWithNullableScalar(val input: Instant?)