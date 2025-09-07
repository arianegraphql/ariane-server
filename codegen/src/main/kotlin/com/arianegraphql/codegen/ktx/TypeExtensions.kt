package com.arianegraphql.codegen.ktx

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.Type

context(ctx: CodegenContext)
val Type<*>.kotlinType: TypeName
    get() = when (this) {
        is graphql.language.TypeName -> nullableType

        is graphql.language.ListType -> listType

        is graphql.language.NonNullType -> nonNullType

        else -> {
            ctx.logger.error("${this::class.java.simpleName} cannot be handled")
            ANY
        }
    }

context(ctx: CodegenContext)
private val graphql.language.ListType.listType: TypeName
    get() = LIST.parameterizedBy(type.kotlinType).copy(nullable = true)

context(ctx: CodegenContext)
private val graphql.language.NonNullType.nonNullType: TypeName
    get() = type.kotlinType.copy(nullable = false)

context(ctx: CodegenContext)
private val graphql.language.TypeName.nullableType: TypeName
    get() = (ctx.scalarTypes[name] ?: ClassName(ctx.packageNameTypes, "${ctx.typePrefix}$name${ctx.typeSuffix}")).copy(
        nullable = true
    )

