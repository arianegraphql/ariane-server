plugins {
    kotlin("jvm")
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

val ktorVersion = "1.5.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    api(project(":server"))

    api("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.4.1")
}
