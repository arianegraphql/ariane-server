package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.arianegraphql.ktx.Resolver
import com.arianegraphql.ktx.ResolverParameters
import com.arianegraphql.ktx.TypeResolverBuilder
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.FieldDefinition
import graphql.language.ObjectTypeDefinition

context(CodegenContext)
fun ObjectTypeDefinition.generateAllResolvers(parent: ClassName): List<FileSpec> {
    val builder = FileSpec.builder(packageNameResolvers, parent.simpleName)

    val argumentsFile = mutableListOf<FileSpec>()

    fieldDefinitions.forEach {
        val argumentClassName = ClassName(packageNameTypes, it.generatedArgumentClassName(parent))
        it.generateResolverFunction(parent, argumentClassName, builder)
        it.generateResolverInterface(parent, argumentClassName, builder)
        argumentsFile += it.generateArgument(parent)
    }

    argumentsFile += builder.build()

    return argumentsFile
}

context(CodegenContext)
fun FieldDefinition.generateResolverFunction(parent: ClassName, argument: ClassName, fileSpecBuilder: FileSpec.Builder) {
    val functionReceiver = TypeResolverBuilder::class.asClassName()
        .parameterizedBy(parent)
        .copy(annotations = emptyList())

    val function = FunSpec.builder(name)
        .receiver(functionReceiver)
        .addParameter(functionalResolverLambdaParameter(parent, argument))
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

fun FieldDefinition.generateResolverInterface(parent: ClassName, argument: ClassName, fileSpecBuilder: FileSpec.Builder) {
    val interfaceName = "${parent.simpleName}${name.replaceFirstChar { it.uppercase() }}Resolver"

    val superType = Resolver::class.asTypeName().parameterizedBy(parent, argument)

    fileSpecBuilder.addType(TypeSpec.interfaceBuilder(interfaceName).addSuperinterface(superType).build())

    val functionReceiver = TypeResolverBuilder::class.asClassName()
        .parameterizedBy(parent)
        .copy(annotations = emptyList())

    val function = FunSpec.builder(name)
        .receiver(functionReceiver)
        .addParameter(
            ParameterSpec.builder(
            RESOLVER_PARAMETER_NAME, ClassName(fileSpecBuilder.packageName, interfaceName)
        ).build())
        .addCode("resolve(%S, $RESOLVER_PARAMETER_NAME)", name)
        .build()


    fileSpecBuilder.addFunction(function)
}

private const val RESOLVER_PARAMETER_NAME = "resolver"