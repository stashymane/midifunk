plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
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

publishing {
    repositories {
        maven("https://repo.stashy.dev")
    }
}
