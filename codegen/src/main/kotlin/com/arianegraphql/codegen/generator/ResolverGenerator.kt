package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.arianegraphql.ktx.ResolverParameters
import com.arianegraphql.ktx.TypeResolverBuilder
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.FieldDefinition
import graphql.language.ObjectTypeDefinition

context(CodegenContext)
fun ObjectTypeDefinition.generateAllResolvers(parent: ClassName): List<FileSpec> {
    val builder = FileSpec.builder(packageNameResolvers, parent.simpleName)

    val argumentsFile = mutableListOf<FileSpec>()

    fieldDefinitions.forEach {
        it.generateResolverFunction(parent, builder)
        argumentsFile += it.generateArgument(parent)
    }

    argumentsFile += builder.build()

    return argumentsFile
}

context(CodegenContext)
fun FieldDefinition.generateResolverFunction(parent: ClassName, fileSpecBuilder: FileSpec.Builder) {
    val argumentClassName = ClassName(packageNameTypes, generatedArgumentClassName(parent))

    val functionReceiver = TypeResolverBuilder::class.asClassName()
        .parameterizedBy(parent)
        .copy(annotations = emptyList())

    val function = FunSpec.builder(name)
        .receiver(functionReceiver)
        .addParameter(functionalResolverLambdaParameter(parent, argumentClassName))
        .addCode("resolve(%S, $RESOLVER_PARAMETER_NAME)", name)
        .build()


    fileSpecBuilder.addFunction(function)
}

private fun functionalResolverLambdaParameter(className: ClassName, argumentType: ClassName): ParameterSpec {
    val receiver = ResolverParameters::class.asClassName().parameterizedBy(className)
    val argumentParam = ParameterSpec.builder("argument", argumentType).build()

    return ParameterSpec.builder(
        RESOLVER_PARAMETER_NAME,
        LambdaTypeName.get(
            receiver = receiver,
            parameters = listOf(argumentParam),
            returnType = typeNameOf<Any?>()
        ).copy(suspending = true),
    ).build()
}

private const val RESOLVER_PARAMETER_NAME = "resolver"