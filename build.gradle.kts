plugins {
	java
	id("com.github.johnrengelman.shadow") version "5.0.0"
	id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
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
	implementation("com.flowpowered:flow-math:1.0.3")
	compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")
	compileOnly("org.jetbrains:annotations:17.0.0")
}

bukkit {
	name = properties["plugin-name"] as String
	main = "com.thevoxelbox.voxelsniper.VoxelSniperPlugin"
	authors = listOf("przerwap", "MikeMatrix", "Gavjenks", "giltwist", "psanker", "Deamon5550", "DivineRage")
	softDepend = listOf("VoxelModPackPlugin")
	apiVersion = "1.13"
}
