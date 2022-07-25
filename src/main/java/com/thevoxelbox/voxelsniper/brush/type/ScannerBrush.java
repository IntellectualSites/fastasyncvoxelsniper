package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;

import java.util.List;
import java.util.stream.Stream;

public class ScannerBrush extends AbstractBrush {

    private static final int DEPTH_MIN = 1;
    private static final int DEPTH_MAX = 64;

    private static final int DEFAULT_DEPTH = 24;

    private int depthMin;
    private int depthMax;

    private int depth;
    private BlockType checkFor;

    @Override
    public void loadProperties() {
        this.depthMin = getIntegerProperty("depth-min", DEPTH_MIN);
        this.depthMax = getIntegerProperty("depth-max", DEPTH_MAX);

        this.depth = getIntegerProperty("default-depth", DEFAULT_DEPTH);
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.info"));
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("d")) {
                    Integer depth = NumericParser.parseInteger(parameters[1]);
                    if (depth != null) {
                        this.depth = depth < this.depthMin ? this.depthMin : Math.min(depth, this.depthMax);
                        messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.set-depth", this.depth));
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
            return super.sortCompletions(Stream.of("d"), parameter, 0);
        }
        return super.handleCompletions(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        scan(snipe, face);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        scan(snipe, face);
    }

    private void scan(Snipe snipe, Direction blockFace) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.checkFor = toolkitProperties.getPattern().asBlockType();
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (blockFace == Direction.NORTH) { // Scan south
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() + i) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.fond", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.SOUTH) { // Scan north
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() - i) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.fond", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.EAST) { // Scan west
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() - i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.fond", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.WEST) { // Scan east
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() + i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.fond", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.UP) { // Scan down
            for (int i = 1; i < this.depth + 1; i++) {
                if ((targetBlock.getY() - i) <= getEditSession().getMinY()) {
                    break;
                }
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() - i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.fond", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.DOWN) { // Scan up
            for (int i = 1; i < this.depth + 1; i++) {
                EditSession editSession = getEditSession();
                if ((targetBlock.getY() + i) >= editSession.getMaxY()) {
                    break;
                }
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() + i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.fond", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .patternMessage()
                .message(Caption.of("voxelsniper.brush.scanner.set-depth", this.depth))
                .send();
    }

}
