plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "net.fredrikmeyer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


application {
    mainClass = "net.fredrikmeyer.MainKt"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20")
    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.12.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation("org.assertj:assertj-swing-junit:3.17.1")
    constraints {
        testImplementation("org.junit:junit:5.12.1")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
kotlin.sourceSets["test"].kotlin.srcDirs("test")
sourceSets["main"].resources.srcDirs("main")
sourceSets["test"].resources.srcDirs("test")