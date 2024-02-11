plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.nexusPublish)
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.stashy.midifunk"
version = "0.8.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.coroutines.core)
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

val test: Test by tasks
test.useJUnitPlatform()

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            pom {
                name.set("Midifunk")
                description.set("A reactive MIDI wrapper library for Kotlin/Java")
                url.set("https://github.com/stashymane/midifunk")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("stashymane")
                        name.set("Albertas Sarutis")
                        email.set("aestheticrice@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/stashymane/midifunk.git")
                    developerConnection.set("scm:git:ssh://git@github.com:stashymane/midifunk.git")
                    url.set("https://github.com/stashymane/midifunk")
                }
            }
            from(components["java"])
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null && signingKeyId != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications["mavenJava"])
    }
}

kotlin {
    jvmToolchain(8)
}
