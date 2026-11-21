package com.thevoxelbox.voxelsniper.util.minecraft;

public class Identifiers {

    public static final String MINECRAFT_IDENTIFIER = "minecraft:";
    public static final int MINECRAFT_IDENTIFIER_LENGTH = 10;

    /**
     * Return the identifier without the {@code minecraft:} namespace, which registries assume when none is given.
     * Identifiers of other namespaces (datapack biomes, plugin entities...) are returned whole: they cannot be parsed
     * back without it.
     *
     * @param identifier the namespaced identifier
     * @return the identifier without the minecraft namespace
     * @since 3.2.6
     */
    public static String withoutMinecraftNamespace(String identifier) {
        return identifier.startsWith(MINECRAFT_IDENTIFIER)
                ? identifier.substring(MINECRAFT_IDENTIFIER_LENGTH)
                : identifier;
    }

}
