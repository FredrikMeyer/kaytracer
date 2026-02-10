plugins {
    kotlin("jvm") version "2.3.10"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.3.0")
    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.assertj:assertj-swing-junit:3.17.1")
    constraints {
        testImplementation("junit:junit:4.13.2")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin.sourceSets["main"].kotlin.srcDirs("main")
kotlin.sourceSets["test"].kotlin.srcDirs("test")
sourceSets["main"].resources.srcDirs("main")
sourceSets["test"].resources.srcDirs("test")