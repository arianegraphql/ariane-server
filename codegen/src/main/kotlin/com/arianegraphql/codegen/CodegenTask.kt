package com.arianegraphql.codegen

import com.arianegraphql.codegen.ktx.clear
import com.arianegraphql.codegen.task.generateCode
import com.arianegraphql.codegen.task.prepare
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class CodegenTask : DefaultTask() {

    @get:InputFile
    abstract val schema: RegularFileProperty

    @get:InputFile
    abstract val configuration: RegularFileProperty

    @get:OutputDirectory
    internal val outputDir = project.layout.buildDirectory.dir("generated/ariane/main/kotlin")

    @TaskAction
    fun run() {
        outputDir.orNull?.asFile?.clear()
        println("[MELVIN] Schema = ${schema.orNull?.asFile?.absolutePath}")
        println("[MELVIN] Config = ${configuration.orNull?.asFile?.absolutePath}")

        val context = prepare().getOrElse {
            logger.error(it.message)
            return
        }

        with(context) {
            generateCode().onFailure {
                logger.error(it.message)
                return
            }
        }

        logger.info("[ARIANE] Code generated")
    }

    companion object {
        const val TaskName = "arianegraphql-codegen"

        val JsonParser = Json { ignoreUnknownKeys = true }
    }
}