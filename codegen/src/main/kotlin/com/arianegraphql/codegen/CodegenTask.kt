package com.arianegraphql.codegen

import com.arianegraphql.codegen.ktx.clear
import com.arianegraphql.codegen.task.generateCode
import com.arianegraphql.codegen.task.prepare
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class CodegenTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val schema: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configuration: RegularFileProperty

    @get:OutputDirectory
    internal val outputDir = project.layout.buildDirectory.dir("generated/ariane/main/kotlin")

    @TaskAction
    fun run() {
        outputDir.orNull?.asFile?.clear()
        logger.info("[ARIANE] Schema = ${schema.orNull?.asFile?.absolutePath}")
        logger.info("[ARIANE] Config = ${configuration.orNull?.asFile?.absolutePath}")

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