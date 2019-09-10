import net.mcparkour.migle.attributes.ApiVersion

plugins {
	java
	id("net.mcparkour.migle.migle-bukkit") version "1.1.0"
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	jcenter()
	maven("https://hub.spigotmc.org/nexus/content/repositories/public") {
		content {
			includeGroup("org.spigotmc")
			includeGroup("net.md-5")
		}
	}
}

dependencies {
	compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:17.0.0")
}

migleBukkit {
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	name = "VoxelSniper"
	apiVersion = ApiVersion.VERSION_1_13
	authors = listOf("przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage", "pitcer", "jaqobb")
	website = "https://github.com/mcparkournet/voxel-sniper-flattened"
	softDepend = listOf("VoxelModPackPlugin")
}
