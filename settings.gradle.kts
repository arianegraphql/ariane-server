pluginManagement {
    // Versions are declared in 'gradle.properties'
    plugins {
        kotlin("jvm") version "1.8.20"
    }
    repositories {
        mavenLocal()
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
include("sample")
include("codegen")
