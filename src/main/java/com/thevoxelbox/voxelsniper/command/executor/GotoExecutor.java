package com.thevoxelbox.voxelsniper.command.executor;

import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.VoxelCommandElement;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command(value = "goto")
@CommandDescription("Warp to the specified coordinates.")
@Permission("voxelsniper.goto")
public class GotoExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public GotoExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("<x> <z>")
    public void onGoto(
            final @NotNull Sniper sniper,
            final int x,
            final int z
    ) {
        Player player = sniper.getPlayer();

        World world = player.getWorld();
        player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
        sniper.print(Caption.of("voxelsniper.command.goto.woosh"));
    }

}
