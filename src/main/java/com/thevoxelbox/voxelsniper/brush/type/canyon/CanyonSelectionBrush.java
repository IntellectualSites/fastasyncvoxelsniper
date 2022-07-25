package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;

public class CanyonSelectionBrush extends CanyonBrush {

    private boolean first = true;
    private int fx;
    private int fz;

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
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
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
