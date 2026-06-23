group = project.rootProject.group
version = project.rootProject.version

plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlinpoet)

    implementation(libs.graphql.java)
    implementation(project(":server"))

    implementation(libs.kotlinx.serialization.json)
}

gradlePlugin {
    website.set("https://arianegraphql.com/docs/codegen")
    vcsUrl.set("https://github.com/arianegraphql/ariane-server")

    plugins {
        create("ariane-graphql-codegen") {
            id = "com.arianegraphql.codegen"
            displayName = "ArianeGraphQL codegen plugin"
            implementationClass = "com.arianegraphql.codegen.CodegenPlugin"
            description = "ArianeGraphQL codegen plugin"
            tags = listOf("graphql")
        }
    }
}