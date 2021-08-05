package com.thevoxelbox.voxelsniper.sniper.toolkit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.TargetBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class BlockTracer {

    private final BlockVector3 targetBlock;
    private final BlockVector3 lastBlock;

    public BlockTracer(Player player, int distance) {
        com.sk89q.worldedit.entity.Player fp = BukkitAdapter.adapt(player);
        TargetBlock tracer = new TargetBlock(fp, distance, 0.2);
        Location targetLocation = tracer.getAnyTargetBlock();
        Location lastLocation = tracer.getPreviousBlock();

        this.targetBlock = targetLocation == null ? null : targetLocation.toBlockPoint();
        this.lastBlock = lastLocation == null ? null : lastLocation.toBlockPoint();
    }

    public @Nullable BlockVector3 getTargetBlock() {
        return this.targetBlock;
    }

    public @Nullable BlockVector3 getLastBlock() {
        return lastBlock;
    }

}
