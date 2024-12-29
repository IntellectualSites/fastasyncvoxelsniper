package com.thevoxelbox.voxelsniper.brush.type.shell;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b shell_ball|shellball|shb")
@CommandPermission("voxelsniper.brush.shellball")
public class ShellBallBrush extends AbstractBrush {

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        bShell(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        bShell(snipe, lastBlock);
    }

    // parameters isn't an abstract method, gilt. You can just leave it out if there are none.
    private void bShell(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        BlockType[][][] oldMaterials = new BlockType[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1]; // Array that holds the original materials plus a buffer
        int blockPositionX = targetBlock.x();
        int blockPositionY = targetBlock.y();
        int blockPositionZ = targetBlock.z();
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
        int brushSizeDoubled = 2 * brushSize;
        Pattern[][][] newMaterials = new Pattern[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];
        for (int x = 0; x <= brushSizeDoubled; x++) {
            for (int y = 0; y <= brushSizeDoubled; y++) {
                System.arraycopy(oldMaterials[x + 1][y + 1], 1, newMaterials[x][y], 0, brushSizeDoubled + 1);
            }
        }
        // Hollow Brush Area
        for (int x = 0; x <= brushSizeDoubled; x++) {
            for (int y = 0; y <= brushSizeDoubled; y++) {
                for (int z = 0; z <= brushSizeDoubled; z++) {
                    int temp = 0;
                    if (oldMaterials[x + 1 + 1][y + 1][z + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1 - 1][y + 1][z + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][y + 1 + 1][z + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][y + 1 - 1][z + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][y + 1][z + 1 + 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (oldMaterials[x + 1][y + 1][z + 1 - 1] == toolkitProperties.getReplacePattern().asBlockType()) {
                        temp++;
                    }
                    if (temp == 0) {
                        newMaterials[x][y][z] = toolkitProperties.getPattern().getPattern();
                    }
                }
            }
        }
        // Make the changes
        double rSquared = Math.pow(brushSize + 0.5, 2);
        for (int x = brushSizeDoubled; x >= 0; x--) {
            double xSquared = Math.pow(x - brushSize, 2);
            for (int y = 0; y <= 2 * brushSize; y++) {
                double ySquared = Math.pow(y - brushSize, 2);
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (xSquared + ySquared + Math.pow(z - brushSize, 2) <= rSquared) {
                        setBlock(
                                blockPositionX - brushSize + x,
                                blockPositionY - brushSize + y,
                                blockPositionZ - brushSize + z,
                                newMaterials[x][y][z]
                        );
                    }
                }
            }
        }
        // This is needed because most uses of this brush will not be visible to the sniper.
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
