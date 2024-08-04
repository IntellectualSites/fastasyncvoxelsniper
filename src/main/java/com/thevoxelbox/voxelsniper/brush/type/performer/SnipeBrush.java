package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b snipe|s")
@Permission("voxelsniper.brush.snipe")
public class SnipeBrush extends AbstractPerformerBrush {

    @Override
    public void loadProperties() {
    }

    @Command("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrushCommand(snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        this.performer.perform(
                getEditSession(),
                targetBlock.getX(),
                targetBlock.getY(),
                targetBlock.getZ(),
                getBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ())
        );
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        this.performer.perform(
                getEditSession(),
                lastBlock.getX(),
                lastBlock.getY(),
                lastBlock.getZ(),
                getBlock(lastBlock.getX(), lastBlock.getY(), lastBlock.getZ())
        );
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .send();
    }

}
