group = project.rootProject.group
version = project.rootProject.version

val ktorVersion = "2.3.4"

dependencies {
    implementation(project(":server"))
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}
