import net.mcparkour.migle.attributes.ApiVersion

plugins {
	java
	id("net.mcparkour.migle.migle-bukkit") version "1.1.1"
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = sourceCompatibility
}

repositories {
	jcenter()
	maven("https://hub.spigotmc.org/nexus/content/repositories/public") {
		content {
			includeGroup("org.spigotmc")
			includeGroup("net.md-5")
		}
	}
	maven { url = uri("https://mvn.intellectualsites.com/content/repositories/releases/") }
}

dependencies {
//	implementation("net.mcparkour:common-math:1.0.0")
//	implementation("net.mcparkour:common-text:1.0.0")
	compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:20.1.0")
	implementation("com.intellectualsites.fawe:FAWE-Bukkit:1.16-448")
}

migleBukkit {
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	name = "VoxelSniper"
	apiVersion = ApiVersion.VERSION_1_13
	authors = listOf("Empire92", "przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage", "pitcer", "jaqobb")
	website = "https://github.com/IntellectualSites/voxel-sniper-flattened"
	softDepend = listOf("VoxelModPackPlugin")
}
