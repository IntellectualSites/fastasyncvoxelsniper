package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b voxel_disc|voxeldisc|vd")
@CommandPermission("voxelsniper.brush.voxel_disc")
public class VoxelDiscBrush extends AbstractPerformerBrush {

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
        disc(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        disc(snipe, lastBlock);
    }

    private void disc(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= -toolkitProperties.getBrushSize(); x--) {
            for (int z = toolkitProperties.getBrushSize(); z >= -toolkitProperties.getBrushSize(); z--) {
                this.performer.perform(
                        getEditSession(),
                        blockX + x,
                        blockY,
                        blockZ + z,
                        getBlock(blockX + x, blockY, blockZ + z)
                );
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .send();
    }

}
