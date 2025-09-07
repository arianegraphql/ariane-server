package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.*
import graphql.language.FieldDefinition

context(ctx: CodegenContext)
fun FieldDefinition.generateArgument(parent: ClassName, fileSpecBuilder: FileSpec.Builder) {
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

    fileSpecBuilder.addType(classSpec)
}

fun FieldDefinition.generatedArgumentClassName(parent: ClassName) = "${parent.simpleName}${name.replaceFirstChar { it.uppercase() }}Argument"