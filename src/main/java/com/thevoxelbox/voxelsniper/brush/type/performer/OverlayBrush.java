package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Range;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b overlay|over")
@Permission("overlay")
public class OverlayBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_DEPTH = 3;

    private boolean allBlocks;

    private int depth;

    @Override
    public void loadProperties() {
        this.depth = getIntegerProperty("default-depth", DEFAULT_DEPTH);
    }

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.overlay.info"));
    }

    @Command("all")
    public void onBrushAll(
            final @NotNull Snipe snipe
    ) {
        this.allBlocks = true;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.overlay.set-overlay-all",
                VoxelSniperText.getStatus(true)
        ));
    }

    @Command("some")
    public void onBrushSome(
            final @NotNull Snipe snipe
    ) {
        this.allBlocks = false;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.overlay.set-overlay-natural",
                VoxelSniperText.getStatus(true)
        ));
    }

    @Command("d <depth>")
    public void onBrushD(
            final @NotNull Snipe snipe,
            final @Argument("depth") @Range(min = "1") int depth
    ) {
        this.depth = depth;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.overlay.set-depth",
                this.depth
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        overlay(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        overlayTwo(snipe);
    }

    private void overlay(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        EditSession editSession = getEditSession();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                // check if column is valid
                // column is valid if it has no solid block right above the clicked layer
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                BlockType type = getBlockType(blockX + x, blockY + 1, blockZ + z);
                if (isIgnoredBlock(type)) {
                    if (Math.pow(x, 2) + Math.pow(z, 2) <= brushSizeSquared) {
                        for (int y = blockY; y >= editSession.getMinY(); y--) {
                            // check for surface
                            BlockType layerBlockType = getBlockType(blockX + x, y, blockZ + z);
                            if (!isIgnoredBlock(layerBlockType)) {
                                for (int currentDepth = y; y - currentDepth < this.depth; currentDepth--) {
                                    BlockType currentBlockType = getBlockType(blockX + x, currentDepth, blockZ + z);
                                    if (isOverrideableMaterial(currentBlockType)) {
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(currentDepth),
                                                blockZ + z,
                                                clampY(blockX + x, currentDepth, blockZ + z)
                                        );
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isIgnoredBlock(BlockType type) {
        return type == BlockTypes.WATER || type == BlockTypes.CACTUS || type.getMaterial().isTranslucent();
    }

    private boolean isOverrideableMaterial(BlockType type) {
        if (this.allBlocks && !Materials.isEmpty(type)) {
            return true;
        }
        return MaterialSets.OVERRIDEABLE.contains(type);
    }

    private void overlayTwo(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        EditSession editSession = getEditSession();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        boolean[][] memory = new boolean[brushSize * 2 + 1][brushSize * 2 + 1];
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                boolean surfaceFound = false;
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y >= editSession.getMinY() && !surfaceFound; y--) { // start scanning from the height you clicked at
                    boolean noSurfaceFound = !memory[x + brushSize][z + brushSize];
                    if (noSurfaceFound) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
                            if (!Materials.isEmpty(getBlockType(
                                    blockX + x,
                                    y - 1,
                                    blockZ + z
                            ))) { // if not a floating block (like one of Notch'world pools)
                                if (Materials.isEmpty(getBlockType(
                                        blockX + x,
                                        y + 1,
                                        blockZ + z
                                ))) { // must start at surface... this prevents it filling stuff in if
                                    // you click in a wall and it starts out below surface.
                                    BlockType type = getBlockType(blockX + x, y, blockZ + z);
                                    if (this.allBlocks) {
                                        boolean nonEmptyMaterial = !Materials.isEmpty(type);
                                        if (nonEmptyMaterial) {
                                            for (int index = 1; (index < this.depth + 1); index++) {
                                                // fills down as many layers as you specify in parameters
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y + index),
                                                        blockZ + z,
                                                        this.clampY(blockX + x, y + index, blockZ + z)
                                                );
                                                // stop it from checking any other blocks in this vertical 1x1 column.
                                                memory[x + brushSize][z + brushSize] = true;
                                            }
                                            surfaceFound = true;
                                        }
                                    } else {
                                        // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                        if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(type)) {
                                            for (int index = 1; (index < this.depth + 1); index++) {
                                                // fills down as many layers as you specify in parameters
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y + index),
                                                        blockZ + z,
                                                        this.clampY(blockX + x, y + index, blockZ + z)
                                                );
                                                // stop it from checking any other blocks in this vertical 1x1 column.
                                                memory[x + brushSize][z + brushSize] = true;
                                            }
                                            surfaceFound = true;
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

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(Caption.of(
                        "voxelsniper.performer-brush.overlay.set-overlay-all",
                        VoxelSniperText.getStatus(this.allBlocks)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.overlay.set-overlay-natural",
                        VoxelSniperText.getStatus(!this.allBlocks)
                ))
                .message(Caption.of(
                        "voxelsniper.performer-brush.overlay.set-depth",
                        this.depth
                ))
                .send();
    }

}
