plugins {
    alias(libs.plugins.kotlin.jvm)
    signing
    `maven-publish`
    alias(libs.plugins.nexus.publish)
}

group = "com.arianegraphql"
version = "0.3.5"


val testLibs = libs

subprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    // Skip signing for codegen module as it's a Gradle plugin with different requirements
    if (name != "codegen") {
        apply(plugin = "signing")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        testRuntimeOnly(testLibs.junit.jupiter.engine)
        testRuntimeOnly(testLibs.junit.platform.launcher)
        testImplementation(testLibs.junit.jupiter)
        testImplementation(testLibs.mockk)
        testImplementation(testLibs.kotlin.faker)
    }

    // Skip publication configuration for codegen module as it's a Gradle plugin
    if (name != "codegen") {
        tasks {
            // published artifacts
            val jarComponent = this@subprojects.components.getByName("java")
            val sourcesJar by registering(Jar::class) {
                archiveClassifier.set("sources")
                from(sourceSets.main.get().allSource)
            }
            val javadocJar by registering(Jar::class) {
                archiveClassifier.set("javadoc")
                from(layout.buildDirectory.dir("javadoc"))
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
}

tasks {
    jar {
        enabled = false
    }
    nexusPublishing {
        repositories {
            sonatype {
                // OSSRH (s01.oss.sonatype.org) was shut down on June 30, 2025;
                // publishing goes through the Central Portal's OSSRH Staging API
                // compatibility service and requires a Central Portal user token.
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
                username.set(project.property("SONATYPE_USERNAME") as String?)
                password.set(project.property("SONATYPE_PASSWORD") as String?)
            }
        }
    }
}
