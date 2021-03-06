group = project.rootProject.group
version = project.rootProject.version

dependencies {
    api("com.graphql-java:graphql-java:17.2")
    api("org.slf4j:slf4j-api:1.7.32")
    api( "ch.qos.logback:logback-classic:1.2.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.5.1")
}