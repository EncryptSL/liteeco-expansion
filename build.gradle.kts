import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm") version "1.9.0" apply true
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.encryptsl.liteeco.papi"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly("encryptsl.cekuj.net:LiteEco:1.4.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
    implementation(kotlin("stdlib", "1.9.0"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    test {
        useJUnitPlatform()
    }

    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/liteeco-expansion.jar"))
    }

    shadowJar {
        minimize()
    }
}