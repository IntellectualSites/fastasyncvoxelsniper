package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandMethod(value = "voxel_chunk|voxelchunk|vchunk")
@CommandDescription("Update the chunk you are standing in.")
@CommandPermission("voxelsniper.sniper")
public class VoxelChunkExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public VoxelChunkExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("")
    public void onVoxelChunk(
            final @NotNull Sniper sniper
    ) {
        Player player = sniper.getPlayer();
        World world = player.getWorld();
        Location location = player.getLocation();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        world.refreshChunk(x, z);
        sniper.print(Caption.of("voxelsniper.command.voxel-chunk.refreshed"));
    }

}
