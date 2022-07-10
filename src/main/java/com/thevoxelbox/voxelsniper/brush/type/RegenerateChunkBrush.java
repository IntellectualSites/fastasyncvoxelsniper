package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
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
            .map(biomeType -> biomeType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
            .toList();

    private BiomeType biomeType = null;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Regenerate Chunk brush:");
            messenger.sendMessage(ChatColor.AQUA + "/b gc [t] -- Sets the selected regen biome type to t.");
            messenger.sendMessage(ChatColor.AQUA + "/b gc list -- Lists all available biomes.");
            messenger.sendMessage(ChatColor.DARK_AQUA + "Currently selected biome type: " + ChatColor.DARK_GREEN +
                    (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(
                            Stream.concat(
                                    Stream.of(((this.biomeType == null) ? ChatColor.GOLD : ChatColor.GRAY) + DEFAULT_BIOME),
                                    BiomeTypes.values().stream()
                                            .map(biomeType -> ((biomeType == this.biomeType) ? ChatColor.GOLD : ChatColor.GRAY) +
                                                    biomeType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
                            ).collect(Collectors.joining(ChatColor.WHITE + ", ",
                                    ChatColor.AQUA + "Available biomes: ", ""
                            ))
                    );
                } else {
                    if (firstParameter.equals(DEFAULT_BIOME)) {
                        this.biomeType = null;
                    } else {
                        BiomeType biomeType = BiomeTypes.get(firstParameter);
                        if (biomeType != null) {
                            this.biomeType = biomeType;
                        } else {
                            messenger.sendMessage(ChatColor.RED + "Invalid biome type.");
                        }
                    }
                    messenger.sendMessage(ChatColor.GOLD + "Biome type set to: " + ChatColor.DARK_GREEN +
                            (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()));
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.concat(
                    Stream.of("list", DEFAULT_BIOME),
                    BIOMES.stream()
            ), parameter, 0);
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
        int chunkY = targetBlock.getY() >> 8;
        int chunkZ = targetBlock.getZ() >> 4;
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage("Generating that chunk using " + (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()) +
                " biome, this might take a while! " + chunkX + " " + chunkY + " " + chunkZ);
        if (regenerateChunk(chunkX, chunkZ, this.biomeType)) {
            messenger.sendMessage(ChatColor.GREEN + "Successfully generated that chunk! " + chunkX + " " + chunkY + " " + chunkZ);
        } else {
            messenger.sendMessage(ChatColor.RED + "Failed to generate that chunk! " + chunkX + " " + chunkY + " " + chunkZ);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.LIGHT_PURPLE + "Tread lightly.")
                .message(ChatColor.LIGHT_PURPLE + "This brush will melt your spleen and sell your kidneys.")
                .message(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN +
                        (this.biomeType == null ? DEFAULT_BIOME : this.biomeType.getId()))
                .send();
    }

}
