package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b cylinder|c")
@CommandPermission("voxelsniper.brush.cylinder")
public class CylinderBrush extends AbstractPerformerBrush {

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.cylinder.info"));
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

    @CommandMethod("h <height>")
    public void onBrushH(
            final @NotNull Snipe snipe,
            final @Argument("height") int height
    ) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        toolkitProperties.setVoxelHeight(height);

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.cylinder.set-voxel-height",
                toolkitProperties.getVoxelHeight()
        ));
    }

    @CommandMethod("c <center>")
    public void onBrushC(
            final @NotNull Snipe snipe,
            final @Argument("center") int center
    ) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        toolkitProperties.setCylinderCenter(center);

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.cylinder.set-origin",
                toolkitProperties.getCylinderCenter()
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        cylinder(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        cylinder(snipe, lastBlock);
    }

    private void cylinder(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int brushSize = toolkitProperties.getBrushSize();
        int yStartingPoint = targetBlock.getY() + toolkitProperties.getCylinderCenter();
        int yEndPoint = targetBlock.getY() + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }
        EditSession editSession = getEditSession();
        int minHeight = editSession.getMinY();
        if (yStartingPoint < minHeight) {
            yStartingPoint = minHeight;
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-start"));
        } else {
            int maxHeight = editSession.getMaxY();
            if (yStartingPoint > maxHeight) {
                yStartingPoint = maxHeight;
                messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-start"));
            }
        }
        if (yEndPoint < minHeight) {
            yEndPoint = minHeight;
            messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-end"));
        } else {
            int maxHeight = editSession.getMaxY();
            if (yEndPoint > maxHeight) {
                yEndPoint = maxHeight;
                messenger.sendMessage(Caption.of("voxelsniper.warning.brush.off-world-end"));
            }
        }
        int blockX = targetBlock.getX();
        int blockZ = targetBlock.getZ();
        double bSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        for (int y = yEndPoint; y >= yStartingPoint; y--) {
            for (int x = brushSize; x >= 0; x--) {
                double xSquared = Math.pow(x, 2);
                for (int z = brushSize; z >= 0; z--) {
                    if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                        this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                clampY(y),
                                blockZ + z,
                                this.clampY(blockX + x, y, blockZ + z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                clampY(y),
                                blockZ - z,
                                this.clampY(blockX + x, y, blockZ - z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX - x,
                                clampY(y),
                                blockZ + z,
                                this.clampY(blockX - x, y, blockZ + z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX - x,
                                clampY(y),
                                blockZ - z,
                                this.clampY(blockX - x, y, blockZ - z)
                        );
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
                .voxelHeightMessage()
                .cylinderCenterMessage()
                .message(Caption.of(
                        "voxelsniper.brush.parameter.true-circle",
                        VoxelSniperText.getStatus(this.trueCircle)
                ))
                .send();
    }

}
