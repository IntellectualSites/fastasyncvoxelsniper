package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Regenerates the target chunk.
 */
public class RegenerateChunkBrush extends AbstractBrush {

    private static final String DEFAULT_BIOME = "default";

    private BiomeType biomeType = null;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.isEmpty()) {
                continue;
            }
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Regenerate Chunk brush:");
                messenger.sendMessage(ChatColor.AQUA + "/b gc biome");
                printBiomeType(messenger);
                return;
            }
            if (parameter.equals(DEFAULT_BIOME)) {
                this.biomeType = null;
            } else {
                try {
                    this.biomeType = BukkitAdapter.adapt(Biome.valueOf(parameter.toUpperCase()));
                } catch (IllegalArgumentException exception) {
                    messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such biome type.");
                }
            }
            printBiomeType(messenger);
        }
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
        if (this.biomeType == null) {
            messenger.sendMessage("Generating that chunk using " + DEFAULT_BIOME + " biome, this might take a while! " + chunkX + " " + chunkZ);
        } else {
            messenger.sendMessage("Generating that chunk using " + BukkitAdapter.adapt(this.biomeType).name().toLowerCase()
                    + " biome, this might take a while! " + chunkX + " " + chunkZ);
        }
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
        printBiomeType(messenger);
    }

    private void printBiomeType(SnipeMessenger messenger) {
        String printout = Stream.concat(
                Stream.of(((this.biomeType == null) ? ChatColor.GRAY : ChatColor.DARK_GRAY) + DEFAULT_BIOME + ChatColor.WHITE),
                BiomeTypes.values().stream()
                        .map(biomeType -> ((biomeType == this.biomeType) ? ChatColor.GRAY + BukkitAdapter.adapt(biomeType).name()
                                .toLowerCase() : ChatColor.DARK_GRAY + BukkitAdapter.adapt(biomeType).name()
                                .toLowerCase()) + ChatColor.WHITE)
        )
                .collect(Collectors.joining(", "));
        messenger.sendMessage(printout);
    }

}
