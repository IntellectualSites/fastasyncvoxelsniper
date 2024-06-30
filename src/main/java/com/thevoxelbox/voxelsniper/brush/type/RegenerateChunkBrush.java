package com.thevoxelbox.voxelsniper.brush.type;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.minecraft.Identifiers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Regenerates the target chunk.
 */
@RequireToolkit
@Command(value = "brush|b regenerate_chunk|regeneratechunk|rc")
@Permission("voxelsniper.brush.ellipsoid")
public class RegenerateChunkBrush extends AbstractBrush {

    private static final String DEFAULT_BIOME = "default";

    private static final List<String> BIOMES = BiomeTypes.values().stream()
            .map(biomeType -> biomeType.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH))
            .toList();

    private BiomeType biomeType = null;

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.regenerate-chunk.info"));
    }

    @Command("list")
    public void onBrushList(
            final @NotNull Snipe snipe
    ) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(VoxelSniperText.formatListWithCurrent(
                BiomeTypes.values(),
                (type, type2) -> type.getId().compareTo(type2.getId()),
                type -> TextComponent.of(type.getId().substring(Identifiers.MINECRAFT_IDENTIFIER_LENGTH)),
                type -> type,
                this.biomeType,
                "voxelsniper.brush.biome"
        ));
    }

    @Command("default")
    public void onBrushDefault(
            final @NotNull Snipe snipe
    ) {
        this.biomeType = null;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.regenerate-chunk.set-biome",
                Caption.of("voxelsniper.brush.regenerate-chunk.default-biome")
        ));
    }

    @Command("<biome-type>")
    public void onBrushBiometype(
            final @NotNull Snipe snipe,
            final @NotNull @Argument("biome-type") BiomeType biomeType
    ) {
        this.biomeType = biomeType;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.regenerate-chunk.set-biome",
                this.biomeType.getId()
        ));
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
                (this.biomeType == null
                        ? Caption.of("voxelsniper.brush.regenerate-chunk.default-biome")
                        : this.biomeType.getId()),
                chunkX,
                chunkY,
                chunkZ
        ));
        if (regenerateChunk(chunkX, chunkZ, this.biomeType)) {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.regenerate-chunk.generated",
                    chunkX,
                    chunkY,
                    chunkZ
            ));
        } else {
            messenger.sendMessage(Caption.of(
                    "voxelsniper.brush.regenerate-chunk.generate-failed",
                    chunkX,
                    chunkY,
                    chunkZ
            ));
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(Caption.of("voxelsniper.brush.regenerate-chunk.warning"))
                .message(Caption.of(
                        "voxelsniper.brush.regenerate-chunk.set-biome",
                        (this.biomeType == null
                                ? Caption.of("voxelsniper.brush.regenerate-chunk.default-biome")
                                : this.biomeType.getId())
                ))
                .send();
    }

}
