plugins {
    kotlin("jvm")
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

    implementation(project(":server"))
}

sourceSets {
    main {
        kotlin {
            srcDir("build/generated/ariane/main/kotlin")
        }
    }
}