# FastAsyncVoxelSniper

This is a fork of [VoxelSniper](https://github.com/TVPT/VoxelSniper). It adds support for newer versions of [Spigot](https://www.spigotmc.org/) and [Paper](https://papermc.io/) keeping available all features from original version of plugin, but with some optimizations and code cleanup.

## Links

* [Download](https://dev.bukkit.org/projects/favs)
* [Discord](https://discord.gg/intellectualsites)
* [Issues](https://github.com/IntellectualSites/fastasyncvoxelsniper/issues)
* [Wiki](https://intellectualsites.github.io/FastasyncVoxelSniper-Documentation/)
* [JavaDocs](https://intellectualsites.github.io/fastasyncvoxelsniper-javadocs/)

## Contributing
See [here](https://github.com/IntellectualSites/.github/blob/main/CONTRIBUTING.md)

## Maven Deployment
Releases are published to the central repository, snapshots are published to S01 OSS Sonatype.

### Gradle
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.intellectualsites.fastasyncvoxelsniper:fastasyncvoxelsniper:2.6.0")
}
```

### Maven
```xml
<dependency>
    <groupId>com.intellectualsites.fastasyncvoxelsniper</groupId>
    <artifactId>fastasyncvoxelsniper</artifactId>
    <version>2.6.0</version>
    <scope>provided</scope>
</dependency>
```
