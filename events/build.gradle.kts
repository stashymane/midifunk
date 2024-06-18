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
val repo = if (version.toString().endsWith("SNAPSHOT")) snapshotRepo else releaseRepo
val sonatypeUsername = project.findProperty("sonatypeUsername")?.toString()
val sonatypePassword = project.findProperty("sonatypePassword")?.toString()

publishing {
    repositories {
        maven {
            name = "MavenCentral"
            url = uri(repo)

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
    isRequired = false
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}
