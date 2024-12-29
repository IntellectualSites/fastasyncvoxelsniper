package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b disc_face|discface|df")
@CommandPermission("voxelsniper.brush.discface")
public class DiscFaceBrush extends AbstractPerformerBrush {

    private boolean trueCircle;

    @Override
    public void loadProperties() {
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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.brush.disc-face.info"));
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

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        pre(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        pre(snipe, lastBlock);
    }

    private void discUpDown(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        int blockX = targetBlock.x();
        int blockY = targetBlock.y();
        int blockZ = targetBlock.z();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int z = brushSize; z >= 0; z--) {
                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY,
                            blockZ + z,
                            getBlock(blockX + x, blockY, blockZ + z)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY,
                            blockZ - z,
                            getBlock(blockX + x, blockY, blockZ - z)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY,
                            blockZ + z,
                            getBlock(blockX - x, blockY, blockZ + z)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY,
                            blockZ - z,
                            getBlock(blockX - x, blockY, blockZ - z)
                    );
                }
            }
        }
    }

    private void discNorthSouth(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        int blockX = targetBlock.x();
        int blockY = targetBlock.y();
        int blockZ = targetBlock.z();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int y = brushSize; y >= 0; y--) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY + y,
                            blockZ,
                            getBlock(blockX + x, blockY + y, blockZ)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            blockY - y,
                            blockZ,
                            getBlock(blockX + x, blockY - y, blockZ)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY + y,
                            blockZ,
                            getBlock(blockX - x, blockY + y, blockZ)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX - x,
                            blockY - y,
                            blockZ,
                            getBlock(blockX - x, blockY - y, blockZ)
                    );
                }
            }
        }
    }

    private void discEastWest(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        int blockX = targetBlock.x();
        int blockY = targetBlock.y();
        int blockZ = targetBlock.z();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int y = brushSize; y >= 0; y--) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY + x,
                            blockZ + y,
                            getBlock(blockX, blockY + x, blockZ + y)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY + x,
                            blockZ - y,
                            getBlock(blockX, blockY + x, blockZ - y)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY - x,
                            blockZ + y,
                            getBlock(blockX, blockY - x, blockZ + y)
                    );
                    this.performer.perform(
                            getEditSession(),
                            blockX,
                            blockY - x,
                            blockZ - y,
                            getBlock(blockX, blockY - x, blockZ - y)
                    );
                }
            }
        }
    }

    private void pre(Snipe snipe, BlockVector3 targetBlock) {
        BlockVector3 lastBlock = getLastBlock();
        Direction blockFace = getDirection(getTargetBlock(), lastBlock);
        if (blockFace == null) {
            return;
        }
        switch (blockFace) {
            case NORTH, SOUTH -> discNorthSouth(snipe, targetBlock);
            case EAST, WEST -> discEastWest(snipe, targetBlock);
            case UP, DOWN -> discUpDown(snipe, targetBlock);
            default -> {
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
                .send();
    }

}
