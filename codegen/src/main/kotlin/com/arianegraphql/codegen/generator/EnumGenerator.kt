package com.arianegraphql.codegen.generator

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.*
import graphql.language.EnumTypeDefinition

context(ctx: CodegenContext)
fun EnumTypeDefinition.generateFile(): FileSpec {
    val builder = TypeSpec.enumBuilder("${ctx.typePrefix}$name${ctx.typeSuffix}")
    enumValueDefinitions.forEach {
        builder.addEnumConstant(it.name)
    }

    return FileSpec.builder(ctx.packageNameTypes, name)
        .addType(builder.build())
        .build()
}