package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.ktx.asKotlinType
import com.arianegraphql.ktx.RootResolverBuilder
import com.arianegraphql.ktx.TypeResolverBuilder
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.*

fun ObjectTypeDefinition.generateFile(logger: KSPLogger): FileSpec {
    val packageName = "com.arianegraphql.codegen.type"

    val constructor = FunSpec.constructorBuilder()
        .addParameters(fieldDefinitions.map { it.toParameter(logger) })
        .build()

    val classSpec = TypeSpec.classBuilder(name).apply {
        if (fieldDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(fieldDefinitions.map { it.toProperty(logger) })
        .build()

    return FileSpec.builder(packageName, name)
        .addType(classSpec)
        .addFunction(generateRootResolver())
        .build()
}

fun InputObjectTypeDefinition.generateFile(logger: KSPLogger): FileSpec {
    val packageName = "com.arianegraphql.codegen.type"

    val constructor = FunSpec.constructorBuilder()
        .addParameters(inputValueDefinitions.map { it.toParameter(logger) })
        .build()

    val classSpec = TypeSpec.classBuilder(name).apply {
        if (inputValueDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(inputValueDefinitions.map { it.toProperty(logger) })
        .build()

    return FileSpec.builder(packageName, name)
        .addType(classSpec)
        .build()
}

fun FieldDefinition.toParameter(logger: KSPLogger): ParameterSpec {
    val type = this.type.asKotlinType(logger)

    return ParameterSpec.builder(this.name, type)
        .build()
}

fun FieldDefinition.toProperty(logger: KSPLogger): PropertySpec {
    val type = this.type.asKotlinType(logger)

    return PropertySpec
        .builder(name, type)
        .initializer(name)
        .build()
}


fun InputValueDefinition.toParameter(logger: KSPLogger): ParameterSpec {
    val type = this.type.asKotlinType(logger)

    return ParameterSpec.builder(this.name, type)
        .build()
}

fun InputValueDefinition.toProperty(logger: KSPLogger): PropertySpec {
    val type = this.type.asKotlinType(logger)

    return PropertySpec
        .builder(name, type)
        .initializer(name)
        .build()
}

fun ObjectTypeDefinition.generateRootResolver(): FunSpec {
    val functionReceiver = typeNameOf<RootResolverBuilder>().copy(
        annotations = emptyList()
    )

    val className = ClassName("com.arianegraphql.codegen.type", name)

    return FunSpec.builder(name)
        .receiver(functionReceiver)
        .addParameter(builderLambdaParameter(className))
        .addCode("type(%S, $BUILDER_PARAMETER_NAME)", name)
        .build()
}

private fun builderLambdaParameter(className: ClassName) = ParameterSpec.builder(
    BUILDER_PARAMETER_NAME,
    LambdaTypeName.get(
        receiver = TypeResolverBuilder::class.asClassName().parameterizedBy(className),
        returnType = UNIT
    )
).build()

private const val BUILDER_PARAMETER_NAME = "builder"