import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.ajoberstar.grgit.Grgit

plugins {
	java
	`java-library`
    `maven-publish`

    alias(libs.plugins.pluginyml)
	alias(libs.plugins.shadow)
	alias(libs.plugins.grgit)
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
    // Platform expectations
	compileOnlyApi(libs.paper)

    // Annotations
	compileOnly(libs.annotations)

    // Plugins
	compileOnlyApi(libs.faweBukkit)

    // Third party
	implementation(libs.serverlib)
	implementation(libs.bstatsBase)
	implementation(libs.bstatsBukkit)
    implementation(libs.paperlib)
}

tasks.compileJava.configure {
	options.release.set(11)
}

configurations.all {
	attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 16)
}

var buildNumber by extra("")
ext {
    val git: Grgit = Grgit.open {
        dir = File("$rootDir/.git")
    }
    val commit: String? = git.head().abbreviatedId
    buildNumber = if (project.hasProperty("buildnumber")) {
        project.properties["buildnumber"] as String
    } else {
        commit.toString()
    }
}

group = "com.thevoxelbox"
version = String.format("%s-%s", rootProject.version, buildNumber)

bukkit {
	name = "FastAsyncVoxelSniper"
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	authors = listOf("Empire92", "przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550",
            "DivineRage", "pitcer", "jaqobb", "NotMyFault", "Aurelien30000")
	apiVersion = "1.13"
	version = rootProject.version.toString()
	softDepend = listOf("VoxelModPackPlugin")
	depend = listOf("FastAsyncWorldEdit")
	website = "https://dev.bukkit.org/projects/favs"
	description = "World editing from ingame using 3D brushes"
}

tasks.named<ShadowJar>("shadowJar") {
	archiveClassifier.set(null as String?)
	dependencies {
		relocate("org.incendo.serverlib", "com.thevoxelbox.voxelsniper.serverlib") {
			include(dependency("org.incendo.serverlib:ServerLib:2.2.1"))
		}
		relocate("org.bstats", "com.thevoxelbox.voxelsniper.metrics") {
			include(dependency("org.bstats:bstats-bukkit:2.2.1"))
			include(dependency("org.bstats:bstats-base:2.2.1"))
		}
        relocate("io.papermc.lib", "com.thevoxelbox.voxelsniper.paperlib") {
            include(dependency("io.papermc:paperlib:1.0.6"))
        }
	}
	minimize()
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
        opt.links("https://papermc.io/javadocs/paper/1.17/")
        opt.links("https://ci.athion.net/job/FastAsyncWorldEdit-1.17-Core-Javadocs/javadoc/")
        opt.links("https://javadoc.io/doc/org.jetbrains/annotations/22.0.0/")
    }

    jar {
        this.archiveClassifier.set("jar")
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
        this.archiveFileName.set("${project.name}-${project.version}.${this.archiveExtension.getOrElse("jar")}")
        this.destinationDirectory.set(rootProject.tasks.shadowJar.get().destinationDirectory.get())
    }

    named("build") {
        dependsOn(named("shadowJar"))
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
                    developer {
                        id.set("Aurelien30000")
                        name.set("Aurelien30000")
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
