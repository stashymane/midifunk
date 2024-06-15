plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)

            implementation(projects.events)
            implementation(projects.devices)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

publishing {
    repositories {
        maven("https://repo.stashy.dev")
    }
}
