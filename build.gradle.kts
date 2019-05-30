import net.mcparkour.migle.attribute.ApiVersionAttribute

plugins {
	java
	id("net.mcparkour.migle.migle-paper") version "1.0.3"
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

repositories {
	jcenter()
	maven("https://papermc.io/repo/repository/maven-public")
}

dependencies {
	compileOnly("com.destroystokyo.paper:paper-api:1.14.2-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:17.0.0")
}

miglePaper {
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	name = "VoxelSniper"
	apiVersion = ApiVersionAttribute.VERSION_1_14
	authors = listOf("przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage", "pitcer", "jaqobb")
	website = "https://github.com/mcparkournet/voxel-sniper-flattened"
	softDepend = listOf("VoxelModPackPlugin")
}
