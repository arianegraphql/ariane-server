package com.arianegraphql.codegen.generator

import com.arianegraphql.ktx.ResolverParameters
import com.arianegraphql.ktx.TypeResolverBuilder
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.FieldDefinition

fun generateResolverFunction(parent: ClassName, type: FieldDefinition): FileSpec {
    val parentTypeName = parent.simpleName
    val packageName = "com.arianegraphql.codegen.resolver.${parentTypeName.replaceFirstChar { it.lowercase() }}"
    val fileName = type.name

    val functionReceiver = TypeResolverBuilder::class.asClassName().parameterizedBy(parent).copy(
        annotations = emptyList()
    )

    return FileSpec.builder(packageName, fileName)
        .addFunction(
            FunSpec.builder(type.name)
                .receiver(functionReceiver)
                .addParameter(functionalResolverLambdaParameter(parent))
                .addCode("resolve(%S, $RESOLVER_PARAMETER_NAME)", type.name)
                .build()
        )
        .build()

}

private fun functionalResolverLambdaParameter(className: ClassName) = ParameterSpec.builder(
    RESOLVER_PARAMETER_NAME,
    LambdaTypeName.get(
        receiver = ResolverParameters::class.asClassName().parameterizedBy(className),
        returnType = typeNameOf<Any?>()
    ).copy(suspending = true)
).build()

private const val RESOLVER_PARAMETER_NAME = "resolver"