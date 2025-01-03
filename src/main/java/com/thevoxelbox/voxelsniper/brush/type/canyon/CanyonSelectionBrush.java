package com.thevoxelbox.voxelsniper.brush.type.canyon;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.command.argument.annotation.DynamicRange;
import com.thevoxelbox.voxelsniper.command.argument.annotation.RequireToolkit;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.jetbrains.annotations.NotNull;

@RequireToolkit
@CommandMethod(value = "brush|b canyon_selection|canyonselection|cas")
@CommandPermission("voxelsniper.brush.canyonselection")
public class CanyonSelectionBrush extends CanyonBrush {

    private boolean first = true;
    private int fx;
    private int fz;

    @CommandMethod("")
    public void onBrush(
            final @NotNull Snipe snipe
    ) {
        super.onBrush(snipe);
    }

    @CommandMethod("info")
    public void onBrushInfo(
            final @NotNull Snipe snipe
    ) {
        super.onBrushInfo(snipe);
    }

    @CommandMethod("y <y-level>")
    public void onBrushY(
            final @NotNull Snipe snipe,
            final @Argument("y-level") @DynamicRange(min = "shiftLevelMin", max = "shiftLevelMax") int yLevel
    ) {
        super.onBrushY(snipe, yLevel);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        execute(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        execute(snipe);
    }

    private void execute(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.x() >> 4;
        int chunkZ = targetBlock.z() >> 4;
        if (this.first) {
            this.fx = chunkX;
            this.fz = chunkZ;
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.first-point"));
        } else {
            messenger.sendMessage(Caption.of("voxelsniper.brush.parameter.second-point"));
            selection(
                    Math.min(this.fx, chunkX),
                    Math.min(this.fz, chunkZ),
                    Math.max(this.fx, chunkX),
                    Math.max(this.fz, chunkZ)
            );
        }
        this.first = !this.first;
    }

    private void selection(int lowX, int lowZ, int highX, int highZ) {
        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                canyon(x, z);
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        super.sendInfo(snipe);
    }

}
