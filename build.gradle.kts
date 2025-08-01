import com.vanniktech.maven.publish.SonatypeHost
import groovy.json.JsonSlurper
import java.net.URI
import io.papermc.hangarpublishplugin.model.Platforms
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    java
    `java-library`
    signing

    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.publish)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)
    alias(libs.plugins.runPaper)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation(platform("com.intellectualsites.bom:bom-newest:1.55"))
    // Platform expectations
    compileOnly(libs.paper)
    compileOnly("org.apache.logging.log4j:log4j-api")

    // Annotations
    compileOnly(libs.annotations)

    // Plugins
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")

    // Cloud command system
    implementation(libs.cloudcore)
    implementation(libs.cloudannotations)
    implementation(libs.cloudbukkit)
    implementation(libs.cloudpaper)

    // Third party
    implementation("dev.notmyfault.serverlib:ServerLib")
    implementation("org.bstats:bstats-base")
    implementation("org.bstats:bstats-bukkit")
    implementation("io.papermc:paperlib")
    implementation("com.intellectualsites.paster:Paster")
}

tasks.compileJava.configure {
    options.release.set(21)
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
}

group = "com.intellectualsites.fastasyncvoxelsniper"
version = "3.2.4-SNAPSHOT"

bukkit {
    name = "FastAsyncVoxelSniper"
    main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
    authors = listOf("Empire92", "przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550",
            "DivineRage", "pitcer", "jaqobb", "NotMyFault", "Aurelien30000")
    apiVersion = "1.20"
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
        val opt = options as StandardJavadocDocletOptions
        opt.links("https://jd.papermc.io/paper/1.21.4/")
        opt.links("https://intellectualsites.github.io/fastasyncworldedit-javadocs/worldedit-core/")
        opt.noTimestamp()
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
        this.archiveFileName.set("${project.name}-${project.version}.${this.archiveExtension.getOrElse("jar")}")
        this.destinationDirectory.set(rootProject.tasks.shadowJar.get().destinationDirectory.get())
        relocate("cloud.commandframework", "com.thevoxelbox.voxelsniper.cloud")
        relocate("io.leangen.geantyref", "com.thevoxelbox.geantyref")
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

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

signing {
    if (!project.hasProperty("skip.signing") && !version.toString().endsWith("-SNAPSHOT")) {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        signing.isRequired
        sign(publishing.publications)
    }
}

mavenPublishing {
    coordinates(
            groupId = "$group",
            artifactId = project.name,
            version = "${project.version}",
    )
    pom {
        name.set(project.name)
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
                organizationUrl.set("https://github.com/IntellectualSites/")
                email.set("contact(at)notmyfault.dev")
            }
            developer {
                id.set("Aurelien30000")
                name.set("Aurelien30000")
                organization.set("IntellectualSites")
                organizationUrl.set("https://github.com/IntellectualSites/")
            }
        }

        scm {
            url.set("https://github.com/IntellectualSites/fastasyncvoxelsniper")
            connection.set("scm:git:https://github.com/IntellectualSites/fastasyncvoxelsniper.git")
            developerConnection.set("scm:git:git@github.com:IntellectualSites/fastasyncvoxelsniper.git")
            tag.set("${project.version}")
        }

        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/IntellectualSites/fastasyncvoxelsniper/issues/")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}

// Keep in sync with FAWE versions
val supportedVersions = listOf("1.20.2", "1.20.4", "1.20.6", "1.21.3", "1.21.4")

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("fastasyncvoxelsniper")
    versionName.set("${project.version}")
    versionNumber.set("${project.version}")
    versionType.set("release")
    uploadFile.set(file("build/libs/${rootProject.name}-${project.version}.jar"))
    gameVersions.addAll(supportedVersions)
    loaders.addAll(listOf("paper", "purpur", "spigot"))
    dependencies {
        required.project("fastasyncworldedit")
    }
    syncBodyFrom.set(rootProject.file("README.md").readText())
    changelog.set("The changelog is available on GitHub: https://github" +
            ".com/IntellectualSites/fastasyncvoxelsniper/releases/tag/${project.version}")
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        id.set("FastAsyncVoxelSniper")
        channel.set("Release")
        changelog.set("The changelog is available on GitHub: https://github" +
                ".com/IntellectualSites/fastasyncvoxelsniper/releases/tag/${project.version}")
        apiKey.set(System.getenv("HANGAR_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(file("build/libs/${rootProject.name}-${project.version}.jar"))
                platformVersions.set(supportedVersions)
                this.dependencies {
                    hangar("FastAsyncWorldEdit") {
                        required.set(true)
                    }
                }
            }
        }
    }
}

tasks {
    register("cacheLatestFaweArtifact") {
        val lastSuccessfulBuildUrl = uri("https://ci.athion.net/job/FastAsyncWorldEdit/lastSuccessfulBuild/api/json").toURL()
        val artifact = ((JsonSlurper().parse(lastSuccessfulBuildUrl) as Map<*, *>)["artifacts"] as List<*>)
                .map { it as Map<*, *> }
                .map { it["fileName"] as String }
                .first { it -> it.contains("Paper") }
        project.ext["faweArtifact"] = artifact
    }

    supportedVersions.forEach { version ->
        register<RunServer>("runServer-$version") {
            dependsOn(getByName("cacheLatestFaweArtifact"))
            minecraftVersion(version)
            pluginJars(*rootProject.getTasksByName("shadowJar", false).map { (it as Jar).archiveFile }
                    .toTypedArray())
            jvmArgs("-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true")
            downloadPlugins {
                url("https://ci.athion.net/job/FastAsyncWorldEdit/lastSuccessfulBuild/artifact/artifacts/${project.ext["faweArtifact"]}")
            }
            // Run explicitly using JDK 21
            val javaToolchains  = project.extensions.getByType<JavaToolchainService>()
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(21))
            })
            group = "run paper"
            runDirectory.set(file("run-$version"))
        }
    }
}
