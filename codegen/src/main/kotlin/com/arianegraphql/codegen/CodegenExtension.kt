package com.arianegraphql.codegen

import org.gradle.api.Project
import java.io.File

open class CodegenExtension(private val project: Project) {

    internal var schema: File? = null
    internal var configuration: File? = null

    open fun schema(file: File) {
        schema = file
    }

    open fun configuration(file: File) {
        configuration = file
    }

    open fun schema(path: String) = schema(File(path))

    open fun configuration(path: String) = schema(File(path))
}