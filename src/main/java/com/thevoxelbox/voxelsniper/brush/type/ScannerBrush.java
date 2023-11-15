package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b scanner|sc")
@CommandPermission("voxelsniper.brush.scanner")
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.scanner.info"));
    }

    @CommandMethod("d <depth>")
    public void onBrushD(
            final @NotNull Snipe snipe,
            final @Argument("depth") @DynamicRange(min = "depthMin", max = "depthMax") int depth
    ) {
        this.depth = depth;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.scanner.set-depth",
                this.depth
        ));
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
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.found", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.SOUTH) { // Scan north
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() - i) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.found", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.EAST) { // Scan west
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() - i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.found", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.WEST) { // Scan east
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() + i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.found", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.UP) { // Scan down
            EditSession editSession = getEditSession();
            for (int i = 1; i < this.depth + 1 && targetBlock.getY() + i >= editSession.getMinY(); i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() - i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.found", this.checkFor.getId(), i));
                    return;
                }
            }
            messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.not-found"));
        } else if (blockFace == Direction.DOWN) { // Scan up
            EditSession editSession = getEditSession();
            for (int i = 1; i < this.depth + 1 && targetBlock.getY() + i <= editSession.getMaxY(); i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() + i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(Caption.of("voxelsniper.brush.scanner.found", this.checkFor.getId(), i));
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
                .message(Caption.of(
                        "voxelsniper.brush.scanner.set-depth",
                        this.depth
                ))
                .send();
    }

}
