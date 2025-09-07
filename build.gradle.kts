plugins {
    kotlin("jvm") version "2.2.10"
    signing
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "com.arianegraphql"
version = "0.3.5"


subprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    dependencies {
        implementation(kotlin("stdlib"))
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0-M2")
        testImplementation("org.junit.jupiter:junit-jupiter:6.0.0-M2")
        testImplementation("io.mockk:mockk:1.14.5")
        testImplementation("io.github.serpro69:kotlin-faker:2.0.0-rc.11")
    }

    tasks {
        // published artifacts
        val jarComponent = this@subprojects.components.getByName("java")
        val sourcesJar by registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }
        val javadocJar by registering(Jar::class) {
            archiveClassifier.set("javadoc")
            from("$buildDir/javadoc")
        }

        publishing {
            publications {
                withType<MavenPublication> {
                    pom {
                        name.set("${this@subprojects.group}:${this@subprojects.name}")
                        url.set("https://github.com/arianegraphql/ariane-server")
                        description.set("Lightweight Kotlin GraphQL server")
                        licenses {
                            license {
                                name.set("MIT License")
                                url.set("https://github.com/arianegraphql/ariane-server/blob/main/LICENSE")
                            }
                        }
                        developers {
                            developer {
                                id.set("mbiamont")
                                name.set("Melvin Biamont")
                                email.set("melvin@arianegraphql.com")
                                url.set("https://github.com/mbiamont")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/arianegraphql/ariane-server.git")
                            developerConnection.set("scm:git:git://github.com/arianegraphql/ariane-server.git")
                            url.set("https://github.com/arianegraphql/ariane-server")
                        }
                    }
                }
                create<MavenPublication>("mavenJava") {
                    from(jarComponent)
                    // no need to publish sources or javadocs for SNAPSHOT builds
                    artifact(sourcesJar.get())
                    artifact(javadocJar.get())
                }
            }
        }
        signing {
            val signingPassword = project.property("GPG_PASSPHRASE") as String?
            val signingKey = project.property("GPG_SECRET") as String?

            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications["mavenJava"])
        }
    }
}

tasks {
    jar {
        enabled = false
    }
    nexusPublishing {
        repositories {
            sonatype {
                nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
                username.set(project.property("SONATYPE_USERNAME") as String?)
                password.set(project.property("SONATYPE_PASSWORD") as String?)
            }
        }
    }
}
