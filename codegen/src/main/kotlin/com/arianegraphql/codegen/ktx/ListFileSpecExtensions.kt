package com.arianegraphql.codegen.ktx

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

fun List<FileSpec>.writeTo(codeGenerator: CodeGenerator, aggregating: Boolean) {
    forEach {
        it.writeTo(codeGenerator, aggregating)
    }
}