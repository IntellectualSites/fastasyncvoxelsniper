package com.thevoxelbox.voxelsniper.brush.type;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequireToolkit
@CommandMethod(value = "brush|b extrude|ex")
@CommandPermission("voxelsniper.brush.extrude")
public class ExtrudeBrush extends AbstractBrush {

    private boolean trueCircle;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.extrude.info"));
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

    private void extrudeUpOrDown(Snipe snipe, boolean isUp) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    int direction = (isUp ? 1 : -1);
                    for (int y = 0; y < Math.abs(toolkitProperties.getVoxelHeight()); y++) {
                        int tempY = y * direction;
                        BlockVector3 targetBlock = getTargetBlock();
                        int targetBlockX = targetBlock.x();
                        int targetBlockY = targetBlock.y();
                        int targetBlockZ = targetBlock.z();
                        perform(
                                targetBlockX + x,
                                targetBlockY + tempY,
                                targetBlockZ + z,
                                clampY(targetBlockX + x, targetBlockY + tempY, targetBlockZ + z),
                                targetBlockX + x,
                                targetBlockY + tempY + direction,
                                targetBlockZ + z,
                                clampY(targetBlockX + x, targetBlockY + tempY + direction, targetBlockZ + z),
                                toolkitProperties
                        );
                    }
                }
            }
        }
    }

    private void extrudeNorthOrSouth(Snipe snipe, boolean isSouth) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int y = -brushSize; y <= brushSize; y++) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    int direction = isSouth ? 1 : -1;
                    for (int z = 0; z < Math.abs(toolkitProperties.getVoxelHeight()); z++) {
                        int tempZ = z * direction;
                        BlockVector3 targetBlock = this.getTargetBlock();
                        perform(
                                targetBlock.x() + x,
                                targetBlock.y() + y,
                                targetBlock.z() + tempZ,
                                clampY(targetBlock.x() + x, targetBlock.y() + y, targetBlock.z() + tempZ),
                                targetBlock.x() + x,
                                targetBlock.y() + y,
                                targetBlock.z() + tempZ + direction,
                                this.clampY(
                                        targetBlock.x() + x,
                                        targetBlock.y() + y,
                                        targetBlock.z() + tempZ + direction
                                ),
                                toolkitProperties
                        );
                    }
                }
            }
        }
    }

    private void extrudeEastOrWest(Snipe snipe, boolean isEast) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        for (int y = -brushSize; y <= brushSize; y++) {
            double ySquared = Math.pow(y, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if ((ySquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    int direction = isEast ? 1 : -1;
                    for (int x = 0; x < Math.abs(toolkitProperties.getVoxelHeight()); x++) {
                        int tempX = x * direction;
                        BlockVector3 targetBlock = this.getTargetBlock();
                        perform(
                                targetBlock.x() + tempX,
                                targetBlock.y() + y,
                                targetBlock.z() + z,
                                this.clampY(targetBlock.x() + tempX, targetBlock.y() + y, targetBlock.z() + z),
                                targetBlock.x() + tempX + direction,
                                targetBlock.y() + y,
                                targetBlock.z() + z,
                                this.clampY(
                                        targetBlock.x() + tempX + direction,
                                        targetBlock.y() + y,
                                        targetBlock.z() + z
                                ),
                                toolkitProperties
                        );
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private void perform(
            int x1,
            int y1,
            int z1,
            BlockState block1,
            int x2,
            int y2,
            int z2,
            BlockState block2,
            ToolkitProperties toolkitProperties
    ) {
        if (toolkitProperties.isVoxelListContains(getBlock(x1, y1, z1))) {
            setBlock(x2, y2, z2, getBlockType(x1, y1, z1));
            setBlockData(x2, clampY(y2), z2, clampY(x1, y1, z1));
        }
    }

    private void selectExtrudeMethod(Snipe snipe, @Nullable Direction blockFace, boolean towardsUser) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        if (blockFace == null || toolkitProperties.getVoxelHeight() == 0) {
            return;
        }
        switch (blockFace) {
            case UP -> extrudeUpOrDown(snipe, towardsUser);
            case SOUTH -> extrudeNorthOrSouth(snipe, towardsUser);
            case EAST -> extrudeEastOrWest(snipe, towardsUser);
            default -> {
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        selectExtrudeMethod(snipe, getDirection(targetBlock, lastBlock), false);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        selectExtrudeMethod(snipe, getDirection(targetBlock, lastBlock), true);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .voxelHeightMessage()
                .voxelListMessage()
                .message(Caption.of(
                        "voxelsniper.brush.parameter.true-circle",
                        VoxelSniperText.getStatus(this.trueCircle)
                ))
                .send();
    }

}
