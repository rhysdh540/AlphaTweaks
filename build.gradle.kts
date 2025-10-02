import net.fabricmc.loom.task.AbstractRemapJarTask

plugins {
    id("fabric-loom") version ("1.11.+")
    id("ploceus") version ("1.11.+")
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
        forRepository { flatDir { dirs("natives") } }
        filter { includeGroup("natives") }
    }
}

ploceus {
    setGeneration(2)
}

loom.runs {
    named("client") {
        property("mixin.debug.export", "true")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:a1.2.6")
    mappings(ploceus.featherMappings("4"))
    exceptions(ploceus.raven("2"))
    signatures(ploceus.sparrow("2"))
    nests(ploceus.nests("6"))

    modImplementation("net.fabricmc:fabric-loader:0.17.2")

    annotationProcessor("dev.rdh:amnesia:1.2.0")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl.lwjgl") {
            useVersion("2.9.4-nightly-20150209")
        }
    }
}

configurations.minecraftNatives {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.lwjgl.lwjgl" && requested.name == "lwjgl-platform") {
            useTarget("natives:lwjgl-platform:2.9.4-nightly-20150209")
        }

        if (requested.group == "net.java.jinput" && requested.name == "jinput-platform") {
            useTarget("natives:jinput-platform:2.0.5")
        }
    }
}

tasks.withType<AbstractRemapJarTask>().configureEach {
    // ploceus postprocesses the jar with doLast and captures the project >:(
    notCompatibleWithConfigurationCache("ploceus")
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