plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.20"
}

group = "com.arianegraphql"
version = "0.2.2"

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.20-1.0.10")
    implementation("com.squareup:kotlinpoet:1.14.2")

    implementation("com.graphql-java:graphql-java:21.1")
    implementation(project(":server"))

    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
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