import org.apache.commons.lang3.SystemUtils

plugins {
    idea
    java
    kotlin("jvm") version "2.0.0-Beta1"
    id("gg.essential.loom") version "0.10.0.+"
}

val forgeVersion: String by project
val ktorVersion: String by project

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

loom {
    // Use same log4j config if you have one, otherwise remove
    // log4jConfigs.from(file("../log4j2.xml"))
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
    }
}

dependencies {
    // compile against minecraft/forge so this module can reference their classes
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:$forgeVersion")

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Ktor (websockets) - this will pull in the ktor engine you choose
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")

    // You can add other libs you need here
}

// This project will be consumed by the two mods. We don't create a final fat-jar here.

// If you want to publish the artifact to be used by the subprojects as a file dependency,
// Gradle project dependency (implementation(project(":frozencloud-core"))) in the mods will do.