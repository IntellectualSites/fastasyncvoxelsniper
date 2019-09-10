import net.mcparkour.migle.attributes.ApiVersion

plugins {
	java
	id("net.mcparkour.migle.migle-paper") version "1.1.0"
}

java {
	sourceCompatibility = JavaVersion.VERSION_12
	targetCompatibility = JavaVersion.VERSION_12
}

repositories {
	jcenter()
	maven("https://papermc.io/repo/repository/maven-public")
}

dependencies {
	compileOnly("com.destroystokyo.paper:paper-api:1.14.4-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:17.0.0")
}

miglePaper {
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	name = "VoxelSniper"
	apiVersion = ApiVersion.VERSION_1_14
	authors = listOf("przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage", "pitcer", "jaqobb")
	website = "https://github.com/mcparkournet/voxel-sniper-flattened"
	softDepend = listOf("VoxelModPackPlugin")
}
