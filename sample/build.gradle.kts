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
    implementation(project(":server-ktor"))
}
