group = project.rootProject.group
version = project.rootProject.version

plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("plugin.serialization") version "1.8.20"
}

dependencies {
    implementation("com.squareup:kotlinpoet:2.2.0")

    implementation("com.graphql-java:graphql-java:25.0.beta-5")
    implementation(project(":server"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

gradlePlugin {
    website.set("https://arianegraphql.com/docs/codegen")
    vcsUrl.set("https://github.com/arianegraphql/ariane-server")

    plugins {
        create("ariane-graphql-codegen") {
            id = "com.arianegraphql.codegen"
            displayName = "ArianeGraphQL codegen plugin"
            implementationClass = "com.arianegraphql.codegen.CodegenPlugin"
            description = "ArianeGraphQL codegen plugin"
            tags = listOf("graphql")
        }
    }
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}