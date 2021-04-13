import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    id("maven-publish")
    kotlin("jvm") version "1.4.32"
}

group = "dev.stashy.midifunk"
version = "0.3.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.reactivex.rxjava3:rxjava:3.0.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

val test: Test by tasks
test.useJUnitPlatform()

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
