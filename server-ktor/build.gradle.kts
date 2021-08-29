group = project.rootProject.group
version = project.rootProject.version

val ktorVersion = "1.6.2"

dependencies {
    implementation(project(":server"))
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
}
