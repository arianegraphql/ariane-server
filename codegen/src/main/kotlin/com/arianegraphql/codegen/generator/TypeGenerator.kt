package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.arianegraphql.codegen.ktx.kotlinType
import com.arianegraphql.ktx.RootResolverBuilder
import com.arianegraphql.ktx.TypeResolverBuilder
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import graphql.language.*

context(ctx: CodegenContext)
fun ObjectTypeDefinition.generateFile(): FileSpec {
    val constructor = FunSpec.constructorBuilder()
        .addParameters(fieldDefinitions.map { it.asNullableParameter })
        .build()

    val classSpec = TypeSpec.classBuilder("${ctx.typePrefix}$name${ctx.typeSuffix}").apply {
        if (fieldDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(fieldDefinitions.map { it.asNullableProperty })
        .build()

    return FileSpec.builder(ctx.packageNameTypes, name)
        .addType(classSpec)
        .addFunction(generateRootResolver())
        .build()
}

context(ctx: CodegenContext)
fun InputObjectTypeDefinition.generateFile(): FileSpec {
    val constructor = FunSpec.constructorBuilder()
        .addParameters(inputValueDefinitions.map { it.asParameter })
        .build()

    val classSpec = TypeSpec.classBuilder("${ctx.typePrefix}$name${ctx.typeSuffix}").apply {
        if (inputValueDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(inputValueDefinitions.map { it.asProperty })
        .build()

    return FileSpec.builder(ctx.packageNameTypes, name)
        .addType(classSpec)
        .build()
}

context(ctx: CodegenContext)
val FieldDefinition.asNullableParameter: ParameterSpec
    get() = ParameterSpec.builder(this.name, type.kotlinType.copy(nullable = true))
        .defaultValue("null")
        .build()

context(ctx: CodegenContext)
val FieldDefinition.asNullableProperty: PropertySpec
    get() = PropertySpec
        .builder(name, type.kotlinType.copy(nullable = true))
        .initializer(name)
        .build()


context(ctx: CodegenContext)
val InputValueDefinition.asParameter: ParameterSpec
    get() = ParameterSpec.builder(this.name, type.kotlinType)
        .build()

context(ctx: CodegenContext)
val InputValueDefinition.asProperty: PropertySpec
    get() = PropertySpec
        .builder(name, type.kotlinType)
        .initializer(name)
        .build()

context(ctx: CodegenContext)
fun ObjectTypeDefinition.generateRootResolver(): FunSpec {
    val functionReceiver = typeNameOf<RootResolverBuilder>().copy(
        annotations = emptyList()
    )

    val className = ClassName(ctx.packageNameTypes, "${ctx.typePrefix}$name${ctx.typeSuffix}")

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