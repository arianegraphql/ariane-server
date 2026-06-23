group = project.rootProject.group
version = project.rootProject.version

dependencies {
    api(libs.graphql.java)
    api(libs.java.dataloader)
    api(libs.slf4j.api)
    api(libs.logback.classic)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
}