plugins {
    id("fabric-loom") version "1.13-SNAPSHOT"
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
    include(implementation("org.yaml:snakeyaml:2.4")!!)
    include(implementation("one.pkg:sewlia-config:${config_api_version}") {
        exclude(group = "org.yaml")
        exclude(group = "org.slf4j")
    })

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