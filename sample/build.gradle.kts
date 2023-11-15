plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("com.arianegraphql.codegen")
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

    implementation(project(":server"))
    implementation(project(":server-ktor"))
    implementation("io.ktor:ktor-server-cio:2.3.4")

    implementation(project(":codegen"))
    ksp(project(":codegen"))
}

kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("${layout.buildDirectory}/generated/ksp/main/kotlin"),
    )
}

ariane {
    schema(file("src/main/resources/schema.graphql"))
    configuration(file("src/main/resources/codegen.json"))
}

ksp {
    arg("graphql-schema", file("src/main/resources/schema.graphql").path)
    arg("ariane-config", file("src/main/resources/codegen.json").path)
}