import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0-RC"
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "15"
            languageVersion = "1.5"
            apiVersion = "1.5"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}