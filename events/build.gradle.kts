plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
    id("signing")
}

kotlin {
    jvm()
    mingwX64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val releaseRepo: String by project.properties
val snapshotRepo: String by project.properties
val sonatypeUsername = project.findProperty("sonatypeUsername")?.toString()
val sonatypePassword = project.findProperty("sonatypePassword")?.toString()

publishing {
    repositories {
        maven {
            name = "MavenCentral"
            url = if (version.toString().endsWith("SNAPSHOT")) uri(snapshotRepo) else uri(releaseRepo)

            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

val signingKeyId: String? by project
val signingKey: String? by project
val signingPassword: String? by project

signing {
    isRequired = gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}
