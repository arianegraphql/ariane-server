plugins {
    kotlin("jvm")
}

group = "com.arianegraphql"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.graphql-java:graphql-java:16.2")
    api(project(":graphql-ktx"))
    api("org.slf4j:slf4j-api:1.7.30")
    api( "ch.qos.logback:logback-classic:1.2.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.4.3")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("io.github.serpro69:kotlin-faker:1.6.0")
}