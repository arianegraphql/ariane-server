plugins {
    kotlin("jvm")
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    implementation(project(":server"))
    implementation(project(":server-ktor"))
    implementation("io.ktor:ktor-server-cio:1.5.3")
}
