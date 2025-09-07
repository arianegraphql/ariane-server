group = project.rootProject.group
version = project.rootProject.version

val coroutinesVersion = "1.10.2"

dependencies {
    api("com.graphql-java:graphql-java:25.0.beta-5")
    api("org.slf4j:slf4j-api:2.0.17")
    api("ch.qos.logback:logback-classic:1.5.18")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVersion")
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}