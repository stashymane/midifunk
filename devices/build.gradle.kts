plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
    id("signing")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)

            implementation(projects.events)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

signing {
    isRequired = gradle.taskGraph.allTasks.any { it is PublishToMavenRepository }

    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications)
}
