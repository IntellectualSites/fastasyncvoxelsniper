package com.thevoxelbox.voxelsniper.command.executor;

import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelChunkExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public VoxelChunkExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.registerAndGetSniper(player);
        if (sniper == null) {
            return;
        }
        World world = player.getWorld();
        Location location = player.getLocation();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        world.refreshChunk(x, z);
        sniper.print(Caption.of("voxelsniper.command.voxel-chunk.refreshed"));
    }

}
