plugins {
    id("fabric-loom") version "1.13-SNAPSHOT"
    id("com.gradleup.shadow") version "9.3.0"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String
var config_api_version = project.property("config_api_version") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

loom.mixin.defaultRefmapName.set("fnp_patcher.refmap.json")

repositories {
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://mvnc.pkg.one/snapshots") {
        name = "OneSnapshot"
    }
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    //mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("maven.modrinth:krypton:0.2.10")
    include(implementation("one.pkg.velocity_rc:velocity-native:3.4.0-SNAPSHOT")!!)
    include(implementation("org.yaml:snakeyaml:2.4")!!)
    include(implementation("one.pkg:sewlia-config:${config_api_version}") {
        exclude(group = "org.yaml")
        exclude(group = "org.slf4j")
    })
    shadow("org.javassist:javassist:3.30.2-GA")
    shadow("org.ow2.asm:asm:9.9")
    shadow("org.ow2.asm:asm-commons:9.9")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version")!!,
            "loader_version" to project.property("loader_version")!!
        )
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.property("archives_base_name")}" }
    }

    manifest {
        attributes["Main-Class"] = "one.pkg.fnp_patcher.PKMain"
        attributes["Premain-Class"] = "one.pkg.fnp_patcher.PKAgent"
        attributes["Agent-Class"] = "one.pkg.fnp_patcher.PKAgent"
        attributes["Can-Retransform-Classes"] = true
        attributes["Can-Redefine-Classes"] = true
        attributes["Boot-Class-Path"] = ""
    }
}

tasks.shadowJar {
    isZip64 = true
    configurations = listOf(project.configurations.getByName("shadow"))
    archiveClassifier.set("dev")

    relocate("org.objectweb.asm", "one.pkg.fnp_patcher.relocated.asm")
    relocate("javassist", "one.pkg.fnp_patcher.relocated.javassist")
}

tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("remapShadowJar") {
    archiveClassifier.set("v2")
    configurations = listOf(project.configurations.getByName("shadow"))

    dependsOn(tasks.remapJar)

    from(zipTree(tasks.remapJar.flatMap { it.archiveFile }))

    relocate("org.objectweb.asm", "one.pkg.fnp_patcher.relocated.asm")
    relocate("javassist", "one.pkg.fnp_patcher.relocated.javassist")

    manifest {
        attributes["Main-Class"] = "one.pkg.fnp_patcher.PKMain"
        attributes["Premain-Class"] = "one.pkg.fnp_patcher.PKAgent"
        attributes["Agent-Class"] = "one.pkg.fnp_patcher.PKAgent"
        attributes["Can-Retransform-Classes"] = true
        attributes["Can-Redefine-Classes"] = true
        attributes["Boot-Class-Path"] = ""
    }
}

tasks.build {
    finalizedBy(tasks.named("remapShadowJar"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    repositories {
    }
}