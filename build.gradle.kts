import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
	id("java")
	id("java-library")
	id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
	id("com.github.johnrengelman.shadow") version "7.0.0"
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = sourceCompatibility
}

repositories {
	mavenCentral()
	maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
	maven { url = uri("https://mvn.intellectualsites.com/content/repositories/releases/") }
	maven { url = uri("https://mvn.intellectualsites.com/content/repositories/thirdparty/") }
}

dependencies {
	compileOnlyApi("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:20.1.0")
	compileOnlyApi("com.intellectualsites.fawe:FAWE-Bukkit:1.16-637")
	implementation("de.notmyfault:serverlib:1.0.1")
	implementation("org.bstats:bstats-bukkit:2.2.1")
	implementation("org.bstats:bstats-base:2.2.1")
}

group = "com.thevoxelbox"
version = "1.0.4-backward"

bukkit {
	name = "VoxelSniper"
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	authors = listOf("Empire92", "przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage", "pitcer", "jaqobb")
	apiVersion = "1.13"
	version = rootProject.version.toString()
	softDepend = listOf("VoxelModPackPlugin")
	depend = listOf("FastAsyncWorldEdit")
	website = "https://github.com/IntellectualSites/voxel-sniper-flattened"
	description = "World editing from ingame using 3D brushes"
}

tasks.named<ShadowJar>("shadowJar") {
	archiveClassifier.set(null as String?)
	dependencies {
		relocate("de.notmyfault", "com.thevoxelbox.voxelsniper") {
			include(dependency("de.notmyfault:serverlib:1.0.1"))
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
