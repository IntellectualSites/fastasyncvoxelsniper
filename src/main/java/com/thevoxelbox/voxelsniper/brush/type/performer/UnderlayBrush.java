package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b underlay|under")
@CommandPermission("voxelsniper.brush.underlay")
public class UnderlayBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_DEPTH = 3;

    private boolean allBlocks;

    private int depth;

    @Override
    public void loadProperties() {
        this.depth = getIntegerProperty("default-depth", DEFAULT_DEPTH);
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.underlay.info"));
    }

    @CommandMethod("all")
    public void onBrushAll(
            final @NotNull Snipe snipe
    ) {
        this.allBlocks = true;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.underlay.set-underlay-all",
                VoxelSniperText.getStatus(true)
        ));
    }

    @CommandMethod("some")
    public void onBrushSome(
            final @NotNull Snipe snipe
    ) {
        this.allBlocks = false;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.underlay.set-underlay-natural",
                VoxelSniperText.getStatus(true)
        ));
    }

    @CommandMethod("d <depth>")
    public void onBrushD(
            final @NotNull Snipe snipe,
            final @Argument("depth") @Range(min = "1") int depth
    ) {
        this.depth = depth;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.underlay.set-depth",
                this.depth
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        underlay(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        underlay2(snipe);
    }

    private void underlay(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        boolean[][] memory = new boolean[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y < blockY + this.depth; y++) { // start scanning from the height you clicked at
                    boolean noSurfaceFound = !memory[x + brushSize][z + brushSize];
                    if (noSurfaceFound) {
                        // if haven't already found the surface in this column
                        if (Math.pow(x, 2) + Math.pow(z, 2) <= brushSizeSquared) { // if inside of the column...
                            if (this.allBlocks) {
                                for (int i = 0; i < this.depth; i++) {
                                    if (!clampY(blockX + x, y + i, blockZ + z).isAir()) {
                                        // fills down as many layers as you specify in parameters
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y + i),
                                                blockZ + z,
                                                clampY(blockX + x, y + i, blockZ + z)
                                        );
                                        // stop it from checking any other blocks in this vertical 1x1 column.
                                        memory[x + brushSize][z + brushSize] = true;
                                    }
                                }
                            } else {
                                // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                if (MaterialSets.OVERRIDEABLE.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                    for (int i = 0; (i < this.depth); i++) {
                                        if (!clampY(blockX + x, y + i, blockZ + z).isAir()) {
                                            // fills down as many layers as you specify in parameters
                                            this.performer.perform(
                                                    getEditSession(),
                                                    blockX + x,
                                                    clampY(y + i),
                                                    blockZ + z,
                                                    clampY(blockX + x, y + i, blockZ + z)
                                            );
                                            // stop it from checking any other blocks in this vertical 1x1 column.
                                            memory[x + brushSize][z + brushSize] = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void underlay2(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        boolean[][] memory = new boolean[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y < blockY + this.depth; y++) { // start scanning from the height you clicked at
                    boolean noSurfaceFound = !memory[x + brushSize][z + brushSize];
                    if (!noSurfaceFound) {
                        // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
                            BlockType type = getBlockType(blockX + x, y, blockZ + z);
                            if (this.allBlocks) {
                                boolean nonEmptyMaterial = !Materials.isEmpty(type);
                                if (nonEmptyMaterial) {
                                    for (int i = -1; i < this.depth - 1; i++) {
                                        // fills down as many layers as you specify in parameters
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y - i),
                                                blockZ + z,
                                                clampY(blockX + x, y - i, blockZ + z)
                                        );
                                        // stop it from checking any other blocks in this vertical 1x1 column.
                                        memory[x + brushSize][z + brushSize] = true;
                                    }
                                }
                            } else {
                                // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(type)) {
                                    for (int i = -1; i < this.depth - 1; i++) {
                                        // fills down as many layers as you specify in parameters
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y - i),
                                                blockZ + z,
                                                clampY(blockX + x, y - i, blockZ + z)
                                        );
                                        // stop it from checking any other blocks in this vertical 1x1 column.
                                        memory[x + brushSize][z + brushSize] = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.underlay.set-underlay-all",
                        VoxelSniperText.getStatus(this.allBlocks)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.underlay.set-underlay-natural",
                        VoxelSniperText.getStatus(!this.allBlocks)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.underlay.set-depth",
                        this.depth
                ))
                .send();
    }

}
