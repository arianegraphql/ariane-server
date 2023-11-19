group = project.rootProject.group
version = project.rootProject.version

plugins {
    id("java-gradle-plugin")
    kotlin("plugin.serialization") version "1.8.20"
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.14.2")

    implementation("com.graphql-java:graphql-java:21.1")
    implementation(project(":server"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

gradlePlugin {
    plugins {
        create("ariane-graphql-codegen") {
            id = "com.arianegraphql.codegen"
            implementationClass = "com.arianegraphql.codegen.CodegenPlugin"
            description = "ArianeGraphQL codegen plugin"
        }
    }
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}