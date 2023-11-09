package com.arianegraphql.codegen.generator

import com.arianegraphql.ktx.RootResolverBuilder
import com.arianegraphql.ktx.TypeResolverBuilder
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.FieldDefinition
import graphql.language.NonNullType
import graphql.language.ObjectTypeDefinition
import java.math.BigDecimal
import java.math.BigInteger

fun ObjectTypeDefinition.generateFile(logger: KSPLogger): FileSpec {
    val packageName = "com.arianegraphql.codegen.types"

    val constructor = FunSpec.constructorBuilder()
        .addParameters(fieldDefinitions.mapNotNull { it.toParameter(this, logger) })
        .build()

    val classSpec = TypeSpec.classBuilder(name)
        .addModifiers(KModifier.DATA)
        .primaryConstructor(constructor)
        .addProperties(fieldDefinitions.mapNotNull { it.toProperty(this, logger) })
        .build()

    return FileSpec.builder(packageName, name)
        .addType(classSpec)
        .addFunction(generateRootResolver())
        .build()
}

fun FieldDefinition.toParameter(parent: ObjectTypeDefinition, logger: KSPLogger): ParameterSpec? {
    val type = this.type.asKotlinType() ?: run {
        logger.warn("Impossible to generate field ${parent.name}.${name}")
        return null
    }

    return ParameterSpec.builder(this.name, type)
        .build()
}

fun FieldDefinition.toProperty(parent: ObjectTypeDefinition, logger: KSPLogger): PropertySpec? {
    val type = this.type.asKotlinType() ?: run {
        logger.warn("Impossible to generate field ${parent.name}.${name}")
        return null
    }

    return PropertySpec
        .builder(name, type)
        .initializer(name)
        .build()
}

fun graphql.language.Type<*>.asKotlinType(): TypeName? {
    var isNullable = false
    val typeName = when (this) {
        is NonNullType -> {
            (type as graphql.language.TypeName).name
        }

        is graphql.language.TypeName -> {
            isNullable = true
            name
        }

        else -> null
    } ?: return null

    return when (typeName) {
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
        else -> ClassName("com.arianegraphql.codegen.types", typeName)
    }.copy(nullable = isNullable)
}

fun ObjectTypeDefinition.generateRootResolver(): FunSpec {
    val functionReceiver = typeNameOf<RootResolverBuilder>().copy(
        annotations = emptyList()
    )

    val className = ClassName("com.arianegraphql.codegen.types", name)

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