plugins {
    java
    kotlin("jvm") version "2.0.0"
    id("org.polyfrost.defaults.loom") // enables minecraft(), mappings(), forge()
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
}

val forgeVersion: String by project
val ktorVersion: String by project

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

dependencies {
    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
}

//// Optional: sourceSets, toolchain, etc.
//sourceSets {
//    main {
//        output.setResourcesDir(java.classesDirectory)
//    }
//}
