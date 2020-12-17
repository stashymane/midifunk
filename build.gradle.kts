plugins {
    id("java-library")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("com.jfrog.bintray") version "1.8.5"
}

group = "dev.stashy.midifunk"
version = "0.2.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.reactivex.rxjava3:rxjava:3.0.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications(name)
    pkg.apply {
        repo = name
        name = name
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/stashymane/midifunk"
        version.apply {
            name = rootProject.name
        }
    }

    tasks {
        test {
            useJUnitPlatform()
        }
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
}