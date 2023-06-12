# FastAsyncVoxelSniper

[![Join us on Discord](https://img.shields.io/discord/268444645527126017.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/intellectualsites)
[![bStats Servers](https://img.shields.io/bstats/servers/6405)](https://bstats.org/plugin/bukkit/FastAsyncVoxelSniper/6405)
[![Crowdin](https://badges.crowdin.net/e/387f461acd0dfa902cb510bc3da3e0e3/localized.svg)](https://intellectualsites.crowdin.com/fastasyncvoxelsniper)

This is a fork of [VoxelSniper](https://github.com/TVPT/VoxelSniper). It adds support for newer versions of [Spigot](https://www.spigotmc.org/) and [Paper](https://papermc.io/) keeping available all features from original version of plugin, but with optimizations and code cleanup.

### What is FAVS and why should I use it?

FAVS is designed for efficient terrain creation.

- VoxelSniper is the premier long range map editing tool for Minecraft. This mod allows you to edit any block that you can see using an arrow (for replacing) and a gunpowder (for adding).
- It is best used by those with a strong attention to detail and 
  fits a wide array of applications, from making detailed edits to structures with the snipe brush, to making massive terraforming jobs a breeze with some of the earth shattering soft selection tools.
- FastAsyncVoxelSniper allows you to perform operations asynchronously, so you can let a lot of people perform many voxel 
  operations with ease and not hanging your server.
- Simple to setup and use
- Extremely configurable
- Uses minimal CPU/Memory
- Safe for many players to use
- Insanely fast, when using the slowest mode
- Hooks into FAWE to utilize its async features and respects the same protection plugins like FAWE.

## Downloads

Downloads are available on the following platforms:
- [Hangar](https://hangar.papermc.io/IntellectualSites/FastAsyncVoxelSniper)
- [Modrinth](https://modrinth.com/plugin/fastasyncvoxelsniper/)
- [CurseForge](https://www.curseforge.com/minecraft/bukkit-plugins/favs)

## Documentation

* [Wiki](https://intellectualsites.github.io/fastasyncvoxelsniper-documentation/)
* [Translations](https://intellectualsites.crowdin.com/fastasyncvoxelsniper)
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
    compileOnly("com.intellectualsites.fastasyncvoxelsniper:fastasyncvoxelsniper:2.9.0")
}
```

### Maven
```xml
<dependency>
    <groupId>com.intellectualsites.fastasyncvoxelsniper</groupId>
    <artifactId>fastasyncvoxelsniper</artifactId>
    <version>2.9.0</version>
    <scope>provided</scope>
</dependency>
```
