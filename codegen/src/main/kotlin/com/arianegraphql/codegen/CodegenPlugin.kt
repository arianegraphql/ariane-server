package com.arianegraphql.codegen

import org.gradle.api.Plugin
import org.gradle.api.Project

class CodegenPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create("ariane", CodegenConfiguration::class.java)

        project.tasks.register("arianegraphql-codegen", CodeGeneratorTask::class.java) {
            it.schema.set(ext.schema)
            it.configuration.set(ext.configuration)
        }
    }
}