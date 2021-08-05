package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Regenerates the target chunk.
 */
public class RegenerateChunkBrush extends AbstractBrush {

    private static final String DEFAULT_BIOME = "default";
    private static final List<String> BIOMES = BiomeTypes.values().stream()
            .map(BiomeType::getId)
            .collect(Collectors.toList());

    private BiomeType biomeType = null;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];
        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Regenerate Chunk brush:");
            messenger.sendMessage(ChatColor.AQUA + "/b gc biome");
            messenger.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN +
                    (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()));
        } else {
            String biomeName = parameters[0];
            if (biomeName.equals(DEFAULT_BIOME)) {
                this.biomeType = null;
            } else {
                BiomeType biomeType = BiomeTypes.get(biomeName);
                if (biomeType != null) {
                    this.biomeType = biomeType;
                } else {
                    messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such biome type.");
                }
            }
            messenger.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN +
                    (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()));
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("biome"), parameter, 0);
        }
        if (parameters.length == 2) {
            String firstParameter = parameters[0];
            if (firstParameter.equalsIgnoreCase("biome")) {
                String parameter = parameters[1];
                return super.sortCompletions(Stream.concat(
                        Stream.of(DEFAULT_BIOME),
                        BIOMES.stream()
                ), parameter, 1);
            }
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        generateChunk(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        generateChunk(snipe);
    }

    private void generateChunk(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage("Generating that chunk using " + (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()) +
                " biome, this might take a while! " + chunkX + " " + chunkZ);
        if (regenerateChunk(chunkX, chunkZ, this.biomeType)) {
            messenger.sendMessage(ChatColor.GREEN + "Successfully generated that chunk! " + chunkX + " " + chunkZ);
        } else {
            messenger.sendMessage(ChatColor.RED + "Failed to generate that chunk! " + chunkX + " " + chunkZ);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Tread lightly.");
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "This brush will melt your spleen and sell your kidneys.");
        messenger.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN +
                (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()));
    }

}
