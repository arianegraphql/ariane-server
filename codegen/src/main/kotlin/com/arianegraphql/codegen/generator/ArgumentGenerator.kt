package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.*
import graphql.language.FieldDefinition

context(CodegenContext)
fun FieldDefinition.generateArgument(parent: ClassName): FileSpec {
    val className = generatedArgumentClassName(parent)

    val constructor = FunSpec.constructorBuilder()
        .addParameters(inputValueDefinitions.map { it.asParameter })
        .build()

    val classSpec = TypeSpec.classBuilder(className).apply {
        if (inputValueDefinitions.isNotEmpty()) {
            addModifiers(KModifier.DATA)
        }
    }
        .primaryConstructor(constructor)
        .addProperties(inputValueDefinitions.map { it.asProperty })
        .build()

    return FileSpec.builder(packageNameTypes, className)
        .addType(classSpec)
        .build()
}

fun FieldDefinition.generatedArgumentClassName(parent: ClassName) = "${parent.simpleName}${name.replaceFirstChar { it.uppercase() }}Argument"