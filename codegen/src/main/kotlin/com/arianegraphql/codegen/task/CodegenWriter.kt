package com.arianegraphql.codegen.task

import com.arianegraphql.codegen.CodegenContext
import com.squareup.kotlinpoet.FileSpec

context(CodegenContext)
fun FileSpec.write() {
    writeTo(buildDir)
}

context(CodegenContext)
fun List<FileSpec>.write() = forEach { it.write() }