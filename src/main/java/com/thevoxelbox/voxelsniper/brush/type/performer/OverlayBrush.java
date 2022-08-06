package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class OverlayBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_DEPTH = 3;

    private boolean allBlocks;

    private int depth;

    @Override
    public void loadProperties() {
        this.depth = getIntegerProperty("default-depth", DEFAULT_DEPTH);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.overlay.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("all")) {
                    this.allBlocks = true;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.overlay.set-overlay-all",
                            VoxelSniperText.getStatus(true)
                    ));
                } else if (firstParameter.equalsIgnoreCase("some")) {
                    this.allBlocks = false;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.overlay.set-overlay-natural",
                            VoxelSniperText.getStatus(true)
                    ));
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
                }
            } else if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("d")) {
                    Integer depth = NumericParser.parseInteger(parameters[1]);
                    if (depth != null) {
                        this.depth = depth < 1 ? 1 : depth;
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.overlay.set-depth", this.depth));
                    } else {
                        messenger.sendMessage(Caption.of("voxelsniper.error.invalid-number", parameters[1]));
                    }
                } else {
                    messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-parameters"));
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
            return super.sortCompletions(Stream.of("all", "some", "d"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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
        return type == BlockTypes.WATER || type.getMaterial().isTranslucent() || type == BlockTypes.CACTUS;
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
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                boolean surfaceFound = false;
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y >= editSession.getMinY() && !surfaceFound; y--) { // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
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
                                    if (this.allBlocks) {
                                        for (int index = 1; (index < this.depth + 1); index++) {
                                            this.performer.perform(
                                                    getEditSession(),
                                                    blockX + x,
                                                    clampY(y + index),
                                                    blockZ + z,
                                                    this.clampY(blockX + x, y + index, blockZ + z)
                                            ); // fills down as many layers as you specify in
                                            // parameters
                                            memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                        surfaceFound = true;
                                    } else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                        BlockType type = getBlockType(blockX + x, y, blockZ + z);
                                        if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(type)) {
                                            for (int index = 1; (index < this.depth + 1); index++) {
                                                this.performer.perform(
                                                        getEditSession(),
                                                        blockX + x,
                                                        clampY(y + index),
                                                        blockZ + z,
                                                        this.clampY(blockX + x, y + index, blockZ + z)
                                                ); // fills down as many layers as you specify
                                                // in parameters
                                                memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
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
                .message(Caption.of("voxelsniper.performer-brush.overlay.set-depth", this.depth))
                .send();
    }

}
