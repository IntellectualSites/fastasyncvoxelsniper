# FastAsyncVoxelSniper

This is a fork of [Voxel Sniper](https://github.com/TVPT/VoxelSniper). It adds support for newer versions of [Spigot](https://www.spigotmc.org/) and [Paper](https://papermc.io/) keeping available all features from original version of plugin, but with some optimizations and code cleanup.

## Links

* [Download](https://dev.bukkit.org/projects/favs)
* [Discord](https://discord.gg/intellectualsites)
* [Issues](https://github.com/IntellectualSites/FastAsyncVoxelSniper/issues)
* [Wiki](https://voxelsniper.fandom.com/wiki/VoxelSniper_Wiki) (Not maintained by IntellectualSites)
* [JavaDocs](https://ci.athion.net/job/FastAsyncVoxelSniper-Javadocs/)

## Contributing
See [here](https://github.com/IntellectualSites/FastAsyncVoxelSniper/blob/main/CONTRIBUTING.md)

## Maven Deployment
For hosted javadocs, see above.

###Gradle
```kotlin
repositories {
    maven {
        name = "IntellectualSites"
        url = uri("https://mvn.intellectualsites.com/content/groups/public/")
    }
}

dependencies {
    compileOnly("com.thevoxelbox:FastAsyncVoxelSniper:2.1.0")
}
```

###Maven
```xml
<!-- FastAsyncVoxelSniper -->
<repository>
    <id>IntellectualSites</id>
    <url>https://mvn.intellectualsites.com/content/groups/public/</url>
</repository>

<!-- FastAsyncVoxelSniper API -->
<dependency>
    <groupId>com.thevoxelbox</groupId>
    <artifactId>FastAsyncVoxelSniper</artifactId>
    <version>2.1.0</version>
    <scope>provided</scope>
</dependency>
```
