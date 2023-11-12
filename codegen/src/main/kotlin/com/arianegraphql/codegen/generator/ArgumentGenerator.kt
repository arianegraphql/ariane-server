package com.arianegraphql.codegen.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.*
import graphql.language.FieldDefinition

fun FieldDefinition.generateArgument(parent: ClassName, logger: KSPLogger): FileSpec {
    val packageName = "com.arianegraphql.codegen.type"

    val className = generatedArgumentClassName(parent)

    val constructor = FunSpec.constructorBuilder()
        .addParameters(inputValueDefinitions.map { it.toParameter(logger) })
        .build()

    val classSpec = TypeSpec.classBuilder(className).apply {
        if (inputValueDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(inputValueDefinitions.map { it.toProperty(logger) })
        .build()

    return FileSpec.builder(packageName, className)
        .addType(classSpec)
        .build()
}

fun FieldDefinition.generatedArgumentClassName(parent: ClassName) = "${parent.simpleName}${name.replaceFirstChar { it.uppercase() }}Argument"