group = project.rootProject.group
version = project.rootProject.version

dependencies {
    implementation(project(":server"))
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.websockets)
    api(libs.ktor.server.cors)
    implementation(libs.jackson.module.kotlin)
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}