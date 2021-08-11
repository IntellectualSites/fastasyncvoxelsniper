package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BiomeBrush extends AbstractBrush {

    private static final List<String> BIOMES = BiomeTypes.values().stream()
            .map(biomeType -> biomeType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
            .collect(Collectors.toList());

    private BiomeType biomeType = BiomeTypes.PLAINS;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b bio [b] -- Sets the selected biome type to b.");
            messenger.sendMessage(ChatColor.AQUA + "/b bio list -- Lists all available biomes.");
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(
                            BiomeTypes.values().stream()
                                    .map(biomeType -> ((biomeType == this.biomeType) ? ChatColor.GRAY : ChatColor.DARK_GRAY) +
                                            biomeType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
                                    .collect(Collectors.joining(ChatColor.WHITE + ", "))
                    );
                } else {
                    BiomeType biomeType = BiomeTypes.get(firstParameter);

                    if (biomeType != null) {
                        this.biomeType = biomeType;
                        messenger.sendMessage(ChatColor.GOLD + "Biome type set to " + ChatColor.DARK_GREEN + this.biomeType.getId());
                    } else {
                        messenger.sendMessage(ChatColor.RED + "Invalid biome type.");
                    }
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display parameter " +
                        "info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.concat(
                    BIOMES.stream(),
                    Stream.of("list")
            ), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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
        int targetBlockX = targetBlock.getX();
        int targetBlockZ = targetBlock.getZ();
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if (xSquared + Math.pow(z, 2) <= brushSizeSquared) {
                    for (int y = 0; y < editSession.getMaxY() + 1; ++y) {
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
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.biomeType.getId());
    }

}
