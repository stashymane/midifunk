plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish)
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
