pluginManagement {
    // Versions are declared in 'gradle.propertie
    plugins {
        id("com.google.devtools.ksp") version "1.8.20-1.0.10"
        kotlin("jvm") version "1.8.20"
    }
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ariane-graphql"
include("server")
include("server-ktor")
include("sample")
include("codegen")
