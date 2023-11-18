package com.arianegraphql.codegen

import org.gradle.api.Plugin
import org.gradle.api.Project

class CodegenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create("ariane", CodegenExtension::class.java)

        project.tasks.register(CodegenTask.TaskName, CodegenTask::class.java) {
            it.schema.set(ext.schema)
            it.configuration.set(ext.configuration)
        }

        project.tasks.named("compileKotlin").configure {
            it.dependsOn(CodegenTask.TaskName)
        }
    }
}