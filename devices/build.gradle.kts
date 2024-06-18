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
