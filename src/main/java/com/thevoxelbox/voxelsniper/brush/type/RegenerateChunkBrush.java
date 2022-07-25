package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;

import java.util.List;
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
            messenger.sendMessage(Caption.of("voxelsniper.brush.regenerate-chunk.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("list")) {
                    messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                            BiomeTypes.values(),
                            (type, type2) -> type.getId().compareTo(type2.getId()),
                            type -> TextComponent.of(type.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)),
                            type -> type,
                            this.biomeType,
                            "voxelsniper.brush.regenerate-chunk"
                    ));
                } else {
                    if (firstParameter.equals(DEFAULT_BIOME)) {
                        this.biomeType = null;
                    } else {
                        BiomeType biomeType = BiomeTypes.get(firstParameter);
                        if (biomeType != null) {
                            this.biomeType = biomeType;
                        } else {
                            messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-biome", firstParameter));
                        }
                    }
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.brush.regenerate-chunk.set-biome",
                            (this.biomeType == null ? Caption.of("voxelsniper.brush.regenerate-chunk.default-biome") :
                                    this.biomeType.getId())
                    ));
                }
            } else {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters-length"));
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
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.regenerate-chunk.generate",
                (this.biomeType == null ? Caption.of("voxelsniper.brush.regenerate-chunk.default-biome") :
                        this.biomeType.getId()),
                chunkX,
                chunkY,
                chunkZ
        ));
        if (regenerateChunk(chunkX, chunkZ, this.biomeType)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.regenerate-chunk.generated", chunkX, chunkY, chunkZ));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.regenerate-chunk.generate-failed", chunkX, chunkY, chunkZ));
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of("voxelsniper.brush.regenerate-chunk.warning"))
                .message(Caption.of(
                        "voxelsniper.brush.regenerate-chunk.set-biome",
                        (this.biomeType == null ? Caption.of("voxelsniper.brush.regenerate-chunk.default-biome") :
                                this.biomeType.getId())
                ))
                .send();
    }

}
