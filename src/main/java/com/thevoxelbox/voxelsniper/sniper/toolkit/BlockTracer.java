package com.thevoxelbox.voxelsniper.sniper.toolkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BlockTracer {

    private final BlockVector3 targetBlock;

    public BlockTracer(Player player, int distance, boolean useLastBlock) {
        com.sk89q.worldedit.entity.Player fp = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.util.Location location = fp.getBlockTrace(distance, useLastBlock);

        this.targetBlock = location == null ? null : location.toBlockPoint();
    }

    public @Nullable BlockVector3 getTargetBlock() {
        return this.targetBlock;
    }

}
