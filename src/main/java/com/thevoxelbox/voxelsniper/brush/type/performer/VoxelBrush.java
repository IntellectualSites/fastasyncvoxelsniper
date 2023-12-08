package com.thevoxelbox.voxelsniper.brush.type.performer;

import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@Command(value = "brush|b voxel|v")
@Permission("voxelsniper.brush.voxel")
public class VoxelBrush extends AbstractPerformerBrush {

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
        voxel(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        voxel(snipe);
    }

    private void voxel(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        BlockVector3 targetBlock = getTargetBlock();
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                for (int y = brushSize; y >= -brushSize; y--) {
                    this.performer.perform(
                            getEditSession(),
                            blockX + x,
                            clampY(blockY + z),
                            blockZ + y,
                            clampY(blockX + x, blockY + z, blockZ + y)
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
                .send();
    }

}
