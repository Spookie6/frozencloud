plugins {
    idea
    java
    kotlin("jvm") version "2.0.0-Beta1" apply false
}

allprojects {
    group = "dev.frozencloud"
    version = "1.0.0"


    repositories {
        mavenCentral()
        maven("https://repo.spongepowered.org/maven/")
        maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    }
}

subprojects {
    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}