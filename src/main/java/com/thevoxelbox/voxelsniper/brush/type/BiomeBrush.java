package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b biome|bio")
@CommandPermission("voxelsniper.brush.biome")
public class BiomeBrush extends AbstractBrush {

    private static final BiomeType DEFAULT_BIOME_TYPE = BiomeTypes.PLAINS;

    private BiomeType biomeType;

    @Override
    public void loadProperties() {
        this.biomeType = (BiomeType) getRegistryProperty("default-biome-type", BiomeType.REGISTRY, DEFAULT_BIOME_TYPE);
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.biome.info"));
    }

    @CommandMethod("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                BiomeTypes.values(),
                (type, type2) -> type.id().compareTo(type2.id()),
                type -> TextComponent.of(type.id().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)),
                type -> type,
                this.biomeType,
                "voxelsniper.brush.biome"
        ));
    }

    @CommandMethod("<biome-type>")
    public void onBrushBiometype(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("biome-type") BiomeType biomeType
    ) {
        this.biomeType = biomeType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.biome.set-biome",
                this.biomeType.id()
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        biome(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        biome(snipe);
    }

    private void biome(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize, 2);
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockX = targetBlock.x();
        int targetBlockZ = targetBlock.z();
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if (xSquared + Math.pow(z, 2) <= brushSizeSquared) {
                    for (int y = editSession.getMinY(); y <= editSession.getMaxY(); ++y) {
                        setBiome(targetBlockX + x, y, targetBlockZ + z, this.biomeType);
                    }
                }
            }
        }
        int block1X = targetBlockX - brushSize;
        int block2X = targetBlockX + brushSize;
        int block1Z = targetBlockZ - brushSize;
        int block2Z = targetBlockZ + brushSize;
        int chunk1X = block1X >> 4;
        int chunk2X = block2X >> 4;
        int chunk1Z = block1Z >> 4;
        int chunk2Z = block2Z >> 4;
        int lowChunkX = block1X <= block2X ? chunk1X : chunk2X;
        int lowChunkZ = block1Z <= block2Z ? chunk1Z : chunk2Z;
        int highChunkX = block1X >= block2X ? chunk1X : chunk2X;
        int highChunkZ = block1Z >= block2Z ? chunk1Z : chunk2Z;
        for (int x = lowChunkX; x <= highChunkX; x++) {
            for (int z = lowChunkZ; z <= highChunkZ; z++) {
                refreshChunk(x, z);
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of(
                        "voxelsniper.brush.biome.set-biome",
                        this.biomeType.id()
                ))
                .send();
    }

}
