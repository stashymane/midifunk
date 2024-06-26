plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.publish) apply false
}

allprojects {
    group = "dev.stashy.midifunk"
    version = "0.8.0"
}
