package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class UnderlayBrush extends AbstractPerformerBrush {

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
            messenger.sendMessage(Caption.of("voxelsniper.performer-brush.underlay.info"));
        } else {
            if (parameters.length == 1) {
                if (firstParameter.equalsIgnoreCase("all")) {
                    this.allBlocks = true;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.underlay.set-underlay-all",
                            VoxelSniperText.getStatus(true)
                    ));
                } else if (firstParameter.equalsIgnoreCase("some")) {
                    this.allBlocks = false;
                    messenger.sendMessage(Caption.of(
                            "voxelsniper.performer-brush.underlay.set-underlay-natural",
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
                        messenger.sendMessage(Caption.of("voxelsniper.performer-brush.underlay.set-depth", this.depth));
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
            return super.sortCompletions(Stream.of("d", "all", "some"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
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
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y < blockY + this.depth; y++) { // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
                        if (Math.pow(x, 2) + Math.pow(z, 2) <= brushSizeSquared) { // if inside of the column...
                            if (this.allBlocks) {
                                for (int i = 0; i < this.depth; i++) {
                                    if (!clampY(blockX + x, y + i, blockZ + z).isAir()) {
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y + i),
                                                blockZ + z,
                                                clampY(blockX + x, y + i, blockZ + z)
                                        ); // fills down as many layers as you specify in
                                        // parameters
                                        memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                    }
                                }
                            } else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                if (MaterialSets.OVERRIDEABLE.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                    for (int i = 0; (i < this.depth); i++) {
                                        if (!clampY(blockX + x, y + i, blockZ + z).isAir()) {
                                            this.performer.perform(
                                                    getEditSession(),
                                                    blockX + x,
                                                    clampY(y + i),
                                                    blockZ + z,
                                                    clampY(blockX + x, y + i, blockZ + z)
                                            ); // fills down as many layers as you specify in
                                            // parameters
                                            memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
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
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y < blockY + this.depth; y++) { // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
                            if (this.allBlocks) {
                                for (int i = -1; i < this.depth - 1; i++) {
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            clampY(y - i),
                                            blockZ + z,
                                            clampY(blockX + x, y - i, blockZ + z)
                                    ); // fills down as many layers as you specify in
                                    // parameters
                                    memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                }
                            } else {
                                // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                    for (int i = -1; i < this.depth - 1; i++) {
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y - i),
                                                blockZ + z,
                                                clampY(blockX + x, y - i, blockZ + z)
                                        ); // fills down as many layers as you specify in
                                        // parameters
                                        memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
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
                .message(Caption.of("voxelsniper.performer-brush.underlay.set-depth", this.depth))
                .send();
    }

}
