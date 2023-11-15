package com.arianegraphql.codegen

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CodeGeneratorTask : DefaultTask() {

    @get:InputFile
    abstract val schema: RegularFileProperty

    @get:InputFile
    abstract val configuration: RegularFileProperty

    //@get:OutputDirectory
    //abstract val outputDir: DirectoryProperty

    @TaskAction
    fun process() {
        println("[MELVIN] Schema = ${schema.orNull?.asFile?.absolutePath}")
        println("[MELVIN] Config = ${configuration.orNull?.asFile?.absolutePath}")
    }
}