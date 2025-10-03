import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    `java` // for common library
    id("org.polyfrost.multi-version.root")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    kotlin("jvm") version "2.0.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.spongepowered.org/maven/")
        maven("https://repo.polyfrost.cc/releases")
        // add any other repos you used previously
    }

    group = findProperty("group")?.toString() ?: "com.example"
}