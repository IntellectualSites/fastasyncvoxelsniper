package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b ring|ri")
@CommandPermission("voxelsniper.brush.ring")
public class RingBrush extends AbstractPerformerBrush {

    private static final double DEFAULT_INNER_SIZE = 0.0;

    private boolean trueCircle;

    private double innerSize = DEFAULT_INNER_SIZE;

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
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.ring.info"));
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

    @CommandMethod("ir <inner-size>")
    public void onBrushIr(
            final @NotNull Snipe snipe,
            final @Argument("inner-size") double innerSize
    ) {
        this.innerSize = innerSize;

        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(Caption.of(
                "voxelsniper.performer-brush.ring.set-inner-radius",
                this.innerSize
        ));
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        ring(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        ring(snipe, lastBlock);
    }

    private void ring(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double outerSquared = Math.pow(brushSize + (this.trueCircle ? 0.5 : 0), 2);
        double innerSquared = Math.pow(this.innerSize, 2);
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int z = brushSize; z >= 0; z--) {
                double ySquared = Math.pow(z, 2);
                if (xSquared + ySquared <= outerSquared && xSquared + ySquared >= innerSquared) {
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
                        "voxelsniper.performer-brush.ring.set-inner-radius",
                        this.innerSize
                ))
                .send();
    }

}
