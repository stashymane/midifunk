plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.publish)
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
