package com.thevoxelbox.voxelsniper.brush.type.shell;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

public class ShellVoxelBrush extends AbstractBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        vShell(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        vShell(snipe, lastBlock);
    }

    private void vShell(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        BlockType[][][] oldMaterials = new BlockType[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a  buffer
        int blockPositionX = targetBlock.getX();
        int blockPositionY = targetBlock.getY();
        int blockPositionZ = targetBlock.getZ();
        // Log current materials into oldmats
        for (int x = 0; x <= 2 * (brushSize + 1); x++) {
            for (int y = 0; y <= 2 * (brushSize + 1); y++) {
                for (int z = 0; z <= 2 * (brushSize + 1); z++) {
                    oldMaterials[x][y][z] = getBlockType(
                            blockPositionX - brushSize - 1 + x,
                            blockPositionY - brushSize - 1 + y,
                            blockPositionZ - brushSize - 1 + z
                    );
                }
            }
        }
        // Log current materials into newmats
        // Array that holds the hollowed materials
        Pattern[][][] newMaterials = new Pattern[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1];
        int brushSizeSquared = 2 * brushSize;
        for (int x = 0; x <= brushSizeSquared; x++) {
            for (int y = 0; y <= brushSizeSquared; y++) {
                System.arraycopy(oldMaterials[x + 1][y + 1], 1, newMaterials[x][y], 0, brushSizeSquared + 1);
            }
        }
        // Hollow Brush Area
        for (int x = 0; x <= brushSizeSquared; x++) {
            for (int z = 0; z <= brushSizeSquared; z++) {
                for (int y = 0; y <= brushSizeSquared; y++) {
                    int temp = 0;
                    if (oldMaterials[x + 1 + 1][z + 1][y + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1 - 1][z + 1][y + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][z + 1 + 1][y + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][z + 1 - 1][y + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][z + 1][y + 1 + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][z + 1][y + 1 - 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (temp == 0) {
                        newMaterials[x][z][y] = toolkitProperties.getPattern().getPattern();
                    }
                }
            }
        }
        // Make the changes
        for (int x = brushSizeSquared; x >= 0; x--) {
            for (int y = 0; y <= brushSizeSquared; y++) {
                for (int z = brushSizeSquared; z >= 0; z--) {
                    this.setBlock(
                            blockPositionX - brushSize + x,
                            blockPositionY - brushSize + y,
                            blockPositionZ - brushSize + z,
                            newMaterials[x][y][z]
                    );
                }
            }
        }
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of("voxelsniper.brush.shell.completed"));
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
