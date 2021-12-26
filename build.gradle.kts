plugins {
    application
    kotlin("jvm") version "1.6.10"
}

application {
    mainClass.set("io.sebi.twemojiamazing.MainKt")
}


group = "io.sebi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("com.beust:klaxon:5.5")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-apache:1.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}