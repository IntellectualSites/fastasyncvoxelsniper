package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b drain|dr")
@CommandPermission("voxelsniper.brush.drain")
public class DrainBrush extends AbstractBrush {

    private boolean trueCircle;
    private boolean disc;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.drain.info"));
    }

    @CommandMethod("<true-circle>")
    public void onBrushTruecircle(
            final @NotNull Snipe snipe,
            final @Argument("true-circle") @Liberal boolean trueCircle
    ) {
        this.trueCircle = trueCircle;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.parameter.true-circle",
                VoxelSniperText.getStatus(this.trueCircle)
        ));
    }

    @CommandMethod("d")
    public void onBrushD(
            final @NotNull Snipe snipe
    ) {
        this.disc = !this.disc;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.brush.drain.disc",
                VoxelSniperText.getStatus(this.disc)
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        drain(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        drain(snipe);
    }

    private void drain(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockX = targetBlock.getX();
        int targetBlockY = targetBlock.getY();
        int targetBlockZ = targetBlock.getZ();
        if (this.disc) {
            for (int x = brushSize; x >= 0; x--) {
                double xSquared = MathHelper.square(x);
                for (int y = brushSize; y >= 0; y--) {
                    double ySquared = MathHelper.square(y);
                    if (xSquared + ySquared <= brushSizeSquared) {
                        BlockType typePlusPlus = getBlockType(targetBlock.add(x, 0, y));
                        if (Materials.isLiquid(typePlusPlus)) {
                            setBlock(targetBlock.add(x, 0, y), BlockTypes.AIR);
                        }
                        BlockType typePlusMinus = getBlockType(targetBlockX + x, targetBlockY, targetBlockZ - y);
                        if (Materials.isLiquid(typePlusMinus)) {
                            setBlock(targetBlockX + x, targetBlockY, targetBlockZ - y, BlockTypes.AIR);
                        }
                        BlockType typeMinusPlus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ + y);
                        if (Materials.isLiquid(typeMinusPlus)) {
                            setBlock(targetBlockX - x, targetBlockY, targetBlockZ + y, BlockTypes.AIR);
                        }
                        BlockType typeMinusMinus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ - y);
                        if (Materials.isLiquid(typeMinusMinus)) {
                            setBlock(targetBlockX - x, targetBlockY, targetBlockZ - y, BlockTypes.AIR);
                        }
                    }
                }
            }
        } else {
            for (int y = (brushSize + 1) * 2; y >= 0; y--) {
                double ySquared = MathHelper.square(y - brushSize);
                for (int x = (brushSize + 1) * 2; x >= 0; x--) {
                    double xSquared = MathHelper.square(x - brushSize);
                    for (int z = (brushSize + 1) * 2; z >= 0; z--) {
                        if ((xSquared + MathHelper.square(z - brushSize) + ySquared) <= brushSizeSquared) {
                            BlockType type = getBlockType(
                                    targetBlockX + x - brushSize,
                                    targetBlockY + z - brushSize,
                                    targetBlockZ + y - brushSize
                            );
                            if (Materials.isLiquid(type)) {
                                setBlock(
                                        targetBlockX + x - brushSize,
                                        targetBlockY + z - brushSize,
                                        targetBlockZ + y - brushSize,
                                        BlockTypes.AIR
                                );
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
                        "voxelsniper.brush.parameter.true-circle",
                        VoxelSniperText.getStatus(this.trueCircle)
                ))
                .message(Caption.of(
                        "voxelsniper.brush.drain.disc",
                        VoxelSniperText.getStatus(this.disc)
                ))
                .send();
    }

}
