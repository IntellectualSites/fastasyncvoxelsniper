package com.thevoxelbox.voxelsniper.brush.type.shell;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequireToolkit
@CommandMethod(value = "brush|b shell_set|shellset|shs")
@CommandPermission("voxelsniper.brush.shellset")
public class ShellSetBrush extends AbstractBrush {

    private static final int MAX_SIZE = 5000000;

    private int maxSize;
    @Nullable
    private BlockVector3 block;
    private World world;

    @Override
    public void loadProperties() {
        this.maxSize = getIntegerProperty("max-size", MAX_SIZE);
    }

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        SnipeMessenger messenger = snipe.createMessenger();
        if (set(targetBlock, getEditSession().getWorld(), snipe)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        SnipeMessenger messenger = snipe.createMessenger();
        if (set(lastBlock, getEditSession().getWorld(), snipe)) {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
        }
    }

    private boolean set(BlockVector3 block, World world, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        if (this.block == null) {
            this.block = block;
            this.world = world;
            return true;
        } else {
            if (!this.world.getName().equals(world.getName())) {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.different-world"));
                this.block = null;
                return true;
            }
            int x1 = this.block.x();
            int x2 = block.x();
            int y1 = this.block.y();
            int y2 = block.y();
            int z1 = this.block.z();
            int z2 = block.z();
            int lowX = Math.min(x1, x2);
            int lowY = Math.min(y1, y2);
            int lowZ = Math.min(z1, z2);
            int highX = Math.max(x1, x2);
            int highY = Math.max(y1, y2);
            int highZ = Math.max(z1, z2);
            int size = Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY);
            if (size > this.maxSize) {
                messenger.sendMessage(Caption.of("voxelsniper.error.brush.invalid-selection", this.maxSize));
            } else {
                List<BlockVector3> blocks = new ArrayList<>(size / 2);
                for (int y = lowY; y <= highY; y++) {
                    for (int x = lowX; x <= highX; x++) {
                        for (int z = lowZ; z <= highZ; z++) {
                            BlockType replaceBlockDataType = toolkitProperties.getReplacePattern().asBlockType();
                            if (isBlockTypeNotEqual(y, x, z, replaceBlockDataType) && isBlockTypeNotEqual(
                                    y,
                                    x + 1,
                                    z,
                                    replaceBlockDataType
                            ) && isBlockTypeNotEqual(y, x - 1, z, replaceBlockDataType) && isBlockTypeNotEqual(
                                    y,
                                    x,
                                    z + 1,
                                    replaceBlockDataType
                            ) && isBlockTypeNotEqual(y, x, z - 1, replaceBlockDataType) && isBlockTypeNotEqual(
                                    y + 1,
                                    x,
                                    z,
                                    replaceBlockDataType
                            ) && isBlockTypeNotEqual(y - 1, x, z, replaceBlockDataType)) {
                                blocks.add(BlockVector3.at(x, y, z));
                            }
                        }
                    }
                }
                for (BlockVector3 currentBlock : blocks) {
                    Pattern pattern = toolkitProperties.getPattern().getPattern();
                    setBlock(currentBlock, pattern);
                }
                messenger.sendMessage(Caption.of("voxelsniper.brush.shell.completed"));
            }
            this.block = null;
            return false;
        }
    }

    private boolean isBlockTypeNotEqual(int y, int x, int z, BlockType replaceBlockDataType) {
        BlockType blockType = getBlockType(x, y, z);
        return blockType != replaceBlockDataType;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .patternMessage()
                .replacePatternMessage()
                .send();
    }

}
