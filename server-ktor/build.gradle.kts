group = project.rootProject.group
version = project.rootProject.version

val ktorVersion = "3.2.3"

dependencies {
    implementation(project(":server"))
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    api("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}