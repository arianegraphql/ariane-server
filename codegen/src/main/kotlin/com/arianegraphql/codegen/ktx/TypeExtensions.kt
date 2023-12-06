package com.arianegraphql.codegen.ktx

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.NonNullType
import graphql.language.Type

context(CodegenContext)
val Type<*>.kotlinType: TypeName
    get() = when (this) {
        is graphql.language.TypeName -> nullableType

        is graphql.language.ListType -> listType

        is NonNullType -> nonNullType

        else -> {
            logger.error("${this::class.java.simpleName} cannot be handled")
            ANY
        }
    }

context(CodegenContext)
private val graphql.language.ListType.listType: TypeName
    get() = LIST.parameterizedBy(type.kotlinType).copy(nullable = true)

context(CodegenContext)
private val graphql.language.NonNullType.nonNullType: TypeName
    get() = type.kotlinType.copy(nullable = false)

context(CodegenContext)
private val graphql.language.TypeName.nullableType: TypeName
    get() = (scalarTypes[name] ?: ClassName(packageNameTypes, "$typePrefix$name$typeSuffix")).copy(nullable = true)

