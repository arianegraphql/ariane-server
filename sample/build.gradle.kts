plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.arianegraphql.codegen") version "0.3.5"
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core.legacy)

    implementation(project(":server"))
    implementation(project(":server-ktor"))
    implementation(libs.ktor.server.cio)
}

sourceSets {
    main {
        kotlin {
            srcDir("build/generated/ariane/main/kotlin")
        }
    }
}

ariane {
    schema(file("src/main/resources/schema.graphqls"))
    configuration(file("src/main/resources/codegen.json"))
}