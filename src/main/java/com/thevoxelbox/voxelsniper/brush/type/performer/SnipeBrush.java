package com.thevoxelbox.voxelsniper.brush.type.performer;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b snipe|s")
@CommandPermission("voxelsniper.brush.snipe")
public class SnipeBrush extends AbstractPerformerBrush {

    @Override
    public void loadProperties() {
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
        this.performer.perform(
                getEditSession(),
                targetBlock.x(),
                targetBlock.y(),
                targetBlock.z(),
                getBlock(targetBlock.x(), targetBlock.y(), targetBlock.z())
        );
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        this.performer.perform(
                getEditSession(),
                lastBlock.x(),
                lastBlock.y(),
                lastBlock.z(),
                getBlock(lastBlock.x(), lastBlock.y(), lastBlock.z())
        );
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .send();
    }

}
