pluginManagement {
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
include("server-ktor")
include("sample")
include("codegen")
