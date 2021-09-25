import java.net.URI
import org.ec4j.gradle.EditorconfigExtension

plugins {
	java
	`java-library`
    `maven-publish`
    signing

    alias(libs.plugins.pluginyml)
	alias(libs.plugins.shadow)
	alias(libs.plugins.grgit)
    alias(libs.plugins.nexus)
    alias(libs.plugins.editorconfig)
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
	compileOnly(libs.bundles.fawe)

    // Third party
	implementation(libs.serverlib)
	implementation(libs.bstatsBase)
	implementation(libs.bstatsBukkit)
    implementation(libs.paperlib)
    implementation(libs.paster)
}

tasks.compileJava.configure {
	options.release.set(11)
}

configurations.all {
	attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 16)
}

group = "com.fastasyncvoxelsniper"
version = "2.2.3-SNAPSHOT"

bukkit {
	name = "FastAsyncVoxelSniper"
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	authors = listOf("Empire92", "przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550",
            "DivineRage", "pitcer", "jaqobb", "NotMyFault", "Aurelien30000")
	apiVersion = "1.13"
	version = project.version.toString()
	softDepend = listOf("VoxelModPackPlugin")
	depend = listOf("FastAsyncWorldEdit")
	website = "https://dev.bukkit.org/projects/favs"
	description = "World editing from ingame using 3D brushes"
}

tasks {

    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial"))
            options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = "UTF-8"
    }

    javadoc {
        title = project.name + " " + project.version
        val opt = options as StandardJavadocDocletOptions
        opt.addStringOption("Xdoclint:none", "-quiet")
        opt.tags(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:"
        )
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
        relocate("org.incendo.serverlib", "com.thevoxelbox.voxelsniper.serverlib")
        relocate("org.bstats", "com.thevoxelbox.voxelsniper.metrics")
        relocate("io.papermc.lib", "com.thevoxelbox.voxelsniper.paperlib")
        relocate("com.intellectualsites.paster", "com.thevoxelbox.voxelsniper.paster")
        minimize()
    }

    named("build") {
        dependsOn(named("shadowJar"))
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

signing {
    if (!version.toString().endsWith("-SNAPSHOT")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        signing.isRequired
        sign(publishing.publications)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {

                name.set(project.name + " " + project.version)
                description.set("FastAsyncVoxelSniper API for Minecraft world editing from ingame using 3D brushes")
                url.set("https://github.com/IntellectualSites/FastAsyncVoxelSniper")

                licenses {
                    license {
                        name.set("Creative Commons Public License")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("NotMyFault")
                        name.set("NotMyFault")
                        organization.set("IntellectualSites")
                        email.set("contact@notmyfault.dev")
                    }
                    developer {
                        id.set("Aurelien30000")
                        name.set("Aurelien30000")
                        organization.set("IntellectualSites")
                    }
                }

                scm {
                    url.set("https://github.com/IntellectualSites/FastAsyncVoxelSniper")
                    connection.set("scm:https://IntellectualSites@github.com/IntellectualSites/FastAsyncVoxelSniper.git")
                    developerConnection.set("scm:git://github.com/IntellectualSites/FastAsyncVoxelSniper.git")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/IntellectualSites/FastAsyncVoxelSniper/issues")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

configure<EditorconfigExtension> {
    includes = listOf("**/*.java")
}

tasks.named("build").configure {
    dependsOn("editorconfigCheck")
}
