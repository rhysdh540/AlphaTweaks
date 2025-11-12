plugins {
    id("fabric-loom") version ("1.13.+")
    id("ploceus") version ("1.13.+")
    id("maven-publish")
}

group = "dev.rdh"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

repositories {
    exclusiveContent {
        forRepository { maven("https://maven.taumc.org/releases") }
        filter { includeGroup("dev.rdh") }
    }

    exclusiveContent {
        forRepository { maven("https://maven.legacyfabric.net") }
        filter { includeGroup("org.lwjgl.lwjgl") }
    }

    exclusiveContent {
        forRepository {
            ivy("https://github.com/r58Playz/jinput-m1/raw/main/plugins/OSX/bin/") {
                patternLayout { artifact("[artifact]-[revision].[ext]") }
                metadataSources { artifact() }
            }
        }

        filter { includeGroup("net.java.jinput-redirected") }
    }
}

ploceus {
    setIntermediaryGeneration(2)
}

loom.runs {
    named("client") {
        property("mixin.debug.export", "true")
        programArgs("--username", "OiledOrangutan")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:a1.1.2_01")
    mappings(ploceus.featherMappings("4"))
    exceptions(ploceus.raven("2"))
    signatures(ploceus.sparrow("2"))
    nests(ploceus.nests("5"))

    modImplementation("net.fabricmc:fabric-loader:0.18.0")

    annotationProcessor("dev.rdh:amnesia:1.2.0")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl.lwjgl") {
            useVersion("2.9.4+legacyfabric.15")
        }

        if (requested.group == "net.java.jinput" && requested.name == "jinput-platform") {
            useTarget("${requested.group}-redirected:${requested.name}:${requested.version}")
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(inputs.properties)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xplugin:amnesia")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven("https://maven.taumc.org/releases") {
            name = "TauMC"
            credentials {
                username = System.getenv("TAUMC_MAVEN_USERNAME")
                password = System.getenv("TAUMC_MAVEN_PASSWORD")
            }
        }
    }
}