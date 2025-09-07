group = project.rootProject.group
version = project.rootProject.version

dependencies {
    api(libs.graphql.java)
    api(libs.slf4j.api)
    api(libs.logback.classic)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactive)
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}