import java.net.URI

plugins {
    java
    `java-library`
    `maven-publish`
    signing

    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.nexus)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.13"))
    // Platform expectations
    compileOnly("io.papermc.paper:paper-api")
    compileOnly("org.apache.logging.log4j:log4j-api")

    // Annotations
    compileOnly(libs.annotations)

    // Plugins
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")

    // Third party
    implementation("dev.notmyfault.serverlib:ServerLib")
    implementation("org.bstats:bstats-base")
    implementation("org.bstats:bstats-bukkit")
    implementation("io.papermc:paperlib")
    implementation("com.intellectualsites.paster:Paster")
}

tasks.compileJava.configure {
    options.release.set(17)
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
}

group = "com.intellectualsites.fastasyncvoxelsniper"
version = "2.7.1-SNAPSHOT"

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
        opt.links("https://jd.papermc.io/paper/1.19/")
        opt.links("https://intellectualsites.github.io/fastasyncworldedit-javadocs/worldedit-core/")
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
                url.set("https://github.com/IntellectualSites/fastasyncvoxelsniper")

                licenses {
                    license {
                        name.set("GNU General Public License, Version 3.0")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("NotMyFault")
                        name.set("Alexander Brandes")
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
                    url.set("https://github.com/IntellectualSites/fastasyncvoxelsniper")
                    connection.set("scm:https://IntellectualSites@github.com/IntellectualSites/fastasyncvoxelsniper.git")
                    developerConnection.set("scm:git://github.com/IntellectualSites/fastasyncvoxelsniper.git")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/IntellectualSites/fastasyncvoxelsniper/issues/")
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
