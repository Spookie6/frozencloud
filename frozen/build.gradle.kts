@file:Suppress("UnstableApiUsage", "PropertyName")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.polyfrost.gradle.util.noServerRunConfigs

plugins {
    java
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
    id("org.polyfrost.defaults.loom")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom") version "1.3.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val mod_id: String = findProperty("frozen.id")?.toString() ?: "frozen"
val mod_name: String = findProperty("frozen.name")?.toString() ?: "Frozen"
val mod_version: String = findProperty("frozen.version")?.toString() ?: "1.0.0"
val baseGroup: String = project.rootProject.group.toString()
val forgeVersion: String by project

// blossom replacement
blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}

version = mod_version
group = baseGroup

// Source resources go into classes folder (same trick you used earlier)
sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

// Shade configurations: we will include the :frozencloud project into the mod jar
val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}
val modShade: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

repositories {
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.spongepowered.org/maven")
    maven("https://repo.polyfrost.cc/releases")
}

dependencies {
    // OneConfig and DevAuth similar to polyfrost example
    modCompileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha+")
    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")

    // Mixin & launchwrapper for legacy Forge
    compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
    shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")

    // include the shared project in modJar by shading it
    implementation(project(path = ":frozencloud-core", configuration = "namedElements"))
//    shade(project(":frozencloud-core"))
}

// ShadowJar task provider
val shadowJarTask = tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("dev")
    configurations = listOf(shade, modShade)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// RemapJar task uses shadowJarTask
tasks.named<RemapJarTask>("remapJar") {
    inputFile.set(shadowJarTask.flatMap { it.archiveFile })
    archiveClassifier.set("")
}

// Make assemble depend on remapJar
tasks.named("assemble") {
    dependsOn(tasks.named("remapJar"))
}

// Loom configuration (using polyfrost defaults)
loom {
    noServerRunConfigs()

    runs {
        named("client") {
            client() // ensures proper main class and arguments
            ideConfigGenerated(true)
            programArgs("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            property("mixin.debug.export", "true")
        }
    }

    forge {
        mixinConfig("mixins.$mod_id.json")
        accessTransformer(file("src/main/resources/accesstransformer.cfg"))
    }

    mixin {
        defaultRefmapName.set("mixins.$mod_id.refmap.json")
    }
}

tasks {
    // processResources replacements
    processResources {
        inputs.property("modid", mod_id)
        inputs.property("name", mod_name)
        inputs.property("version", mod_version)
        inputs.property("mcversion", "1.8.9")
        inputs.property("basePackage", baseGroup)
        filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json", "mods.toml")) {
            expand(mapOf(
                "modid" to mod_id,
                "name" to mod_name,
                "version" to mod_version,
                "mcversion" to "1.8.9",
                "basePackage" to baseGroup
            ))
        }
    }

    // The remapped jar that gets assembled
    assemble {
        dependsOn(remapJar)
    }
}
