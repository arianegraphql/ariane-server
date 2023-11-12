package com.arianegraphql.codegen.ktx

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.NonNullType
import graphql.language.Type
import java.math.BigDecimal
import java.math.BigInteger

fun Type<*>.asKotlinType(logger: KSPLogger? = null): TypeName {
    return when (this) {
        is graphql.language.TypeName -> kotlinType

        is graphql.language.ListType -> kotlinType

        is NonNullType -> kotlinType

        else -> {
            logger?.error("${this::class.java.simpleName} cannot be handled")
            ANY
        }
    }
}

private val graphql.language.ListType.kotlinType: TypeName
    get() = LIST.parameterizedBy(type.asKotlinType()).copy(nullable = true)

private val graphql.language.NonNullType.kotlinType: TypeName
    get() = type.asKotlinType().copy(nullable = false)

private val graphql.language.TypeName.kotlinType: TypeName
    get() {
        return when (name) {
            "String" -> STRING
            "Boolean" -> BOOLEAN
            "Int" -> INT
            "Float" -> FLOAT
            "ID" -> STRING
            "Long" -> LONG
            "Short" -> SHORT
            "Byte" -> BYTE
            "BigDecimal" -> typeNameOf<BigDecimal>()
            "BigInteger" -> typeNameOf<BigInteger>()
            else -> ClassName("com.arianegraphql.codegen.type", name)
        }.copy(nullable = true)
    }