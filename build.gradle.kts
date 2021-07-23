import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	java
	`java-library`
    `maven-publish`
    
	id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
	id("com.github.johnrengelman.shadow") version "7.0.0"
}

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

repositories {
	mavenCentral()
	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
	maven { url = uri("https://mvn.intellectualsites.com/content/groups/public/") }
}

dependencies {
	compileOnlyApi("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:20.1.0")
	compileOnlyApi("com.fastasyncworldedit:FAWE-Bukkit:1.17-74")
	implementation("org.incendo.serverlib:ServerLib:2.2.1")
	implementation("org.bstats:bstats-bukkit:2.2.1")
	implementation("org.bstats:bstats-base:2.2.1")
}

tasks.compileJava.configure {
	options.release.set(11)
}

configurations.all {
	attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 16)
}

group = "com.thevoxelbox"
version = "2.1.1-SNAPSHOT"

bukkit {
	name = "FastAsyncVoxelSniper"
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	authors = listOf("Empire92", "przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage", "pitcer", "jaqobb")
	apiVersion = "1.13"
	version = rootProject.version.toString()
	softDepend = listOf("VoxelModPackPlugin")
	depend = listOf("FastAsyncWorldEdit")
	website = "https://github.com/IntellectualSites/FastAsyncVoxelSniper"
	description = "World editing from ingame using 3D brushes"
}

tasks.named<ShadowJar>("shadowJar") {
	archiveClassifier.set(null as String?)
	dependencies {
		relocate("org.incendo.serverlib", "com.thevoxelbox.voxelsniper") {
			include(dependency("org.incendo.serverlib:ServerLib:2.2.1"))
		}
		relocate("org.bstats", "com.thevoxelbox.voxelsniper.metrics") {
			include(dependency("org.bstats:bstats-bukkit:2.2.1"))
			include(dependency("org.bstats:bstats-base:2.2.1"))
		}
	}
	minimize()
}

tasks.named("build").configure {
	dependsOn("shadowJar")
}

val javadocDir = rootDir.resolve("docs").resolve("javadoc")
tasks {
    val assembleTargetDir = create<Copy>("assembleTargetDirectory") {
        destinationDir = rootDir.resolve("target")
        into(destinationDir)
        from(withType<Jar>())
    }
    named("build") {
        dependsOn(assembleTargetDir)
    }

    named<Delete>("clean") {
        doFirst {
            rootDir.resolve("target").deleteRecursively()
            javadocDir.deleteRecursively()
        }
    }

    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial"))
            options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    javadoc {
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:"
        )
        opt.destinationDirectory = javadocDir
        opt.addBooleanOption("html5", true)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {

                developers {
                    developer {
                        id.set("NotMyFault")
                        name.set("NotMyFault")
                    }
                }

                scm {
                    url.set("https://github.com/IntellectualSites/FastAsyncVoxelSniper")
                    connection.set("scm:https://IntellectualSites@github.com/IntellectualSites/FastAsyncVoxelSniper.git")
                    developerConnection.set("scm:git://github.com/IntellectualSites/FastAsyncVoxelSniper.git")
                }
            }
        }
    }

    repositories {
        mavenLocal()
        val nexusUsername: String? by project
        val nexusPassword: String? by project
        if (nexusUsername != null && nexusPassword != null) {
            maven {
                val releasesRepositoryUrl = "https://mvn.intellectualsites.com/content/repositories/releases/"
                val snapshotRepositoryUrl = "https://mvn.intellectualsites.com/content/repositories/snapshots/"
                url = uri(
                        if (version.toString().endsWith("-SNAPSHOT")) snapshotRepositoryUrl
                        else releasesRepositoryUrl
                )

                credentials {
                    username = nexusUsername
                    password = nexusPassword
                }
            }
        } else {
            logger.warn("No nexus repository is added; nexusUsername or nexusPassword is null.")
        }
    }
}
