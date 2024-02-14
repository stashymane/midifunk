plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)

            implementation(project(":events"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
