package com.arianegraphql.codegen.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import graphql.language.FieldDefinition
import graphql.language.InputValueDefinition

fun FieldDefinition.generateArgument(parent: ClassName, logger: KSPLogger): FileSpec {
    val packageName = "com.arianegraphql.codegen.type"

    val className = "${parent.simpleName}${name.replaceFirstChar { it.uppercase() }}Argument"

    val constructor = FunSpec.constructorBuilder()
        .addParameters(inputValueDefinitions.mapNotNull { it.toParameter(this, logger) })
        .build()

    val classSpec = TypeSpec.classBuilder(className).apply {
        if (inputValueDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(inputValueDefinitions.mapNotNull { it.toProperty(this, logger) })
        .build()

    return FileSpec.builder(packageName, className)
        .addType(classSpec)
        .build()
}

fun InputValueDefinition.toParameter(parent: FieldDefinition, logger: KSPLogger): ParameterSpec? {
    val type = this.type.asKotlinType() ?: run {
        logger.warn("Impossible to generate field ${parent.name}.${name}")
        return null
    }

    return ParameterSpec.builder(this.name, type)
        .build()
}

fun InputValueDefinition.toProperty(parent: FieldDefinition, logger: KSPLogger): PropertySpec? {
    val type = this.type.asKotlinType() ?: run {
        logger.warn("Impossible to generate field ${parent.name}.${name}")
        return null
    }

    return PropertySpec
        .builder(name, type)
        .initializer(name)
        .build()
}