package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotation.specifier.Liberal;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import com.thevoxelbox.voxelsniper.util.painter.Painters;
import org.jetbrains.annotations.NotNull;

/**
 * A brush that creates a solid ball.
 */
@RequireToolkit
@Command(value = "brush|b ball|b")
@Permission("voxelsniper.brush.ball")
public class BallBrush extends AbstractPerformerBrush {

    private boolean trueCircle;

    @Override
    public void loadProperties() {
    }

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Command("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfoCommand(snipe, Caption.of("voxelsniper.performer-brush.ball.info"));
    }

    @Command("<true-circle>")
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
        ball(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        ball(snipe, lastBlock);
    }

    private void ball(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        Painters.sphere()
                .center(targetBlock)
                .radius(brushSize)
                .trueCircle(this.trueCircle)
                .blockSetter(position -> {
                    BlockState block = clampY(position);
                    this.performer.perform(getEditSession(), position.getX(), clampY(position.getY()), position.getZ(), block);
                })
                .paint();
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
