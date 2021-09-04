import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    signing
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

group = "com.arianegraphql"
version = "0.0.4"

repositories {
    mavenCentral()
}

subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            languageVersion = "1.5"
            apiVersion = "1.5"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
        testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
        testImplementation("io.mockk:mockk:1.10.6")
        testImplementation("io.github.serpro69:kotlin-faker:1.6.0")
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
                            connection.set("scm:git:git://github.com/ExpediaGroup/graphql-kotlin.git")
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
            setRequired {
                gradle.taskGraph.hasTask("publish")
            }

            val signingPassword = project.property("GPG_PASSPHRASE") as String?
            val signingKey= project.property("GPG_SECRET") as String?

            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
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
