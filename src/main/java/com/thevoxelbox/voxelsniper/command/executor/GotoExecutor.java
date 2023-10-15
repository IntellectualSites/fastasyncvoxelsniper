package com.thevoxelbox.voxelsniper.command.executor;

import cloud.commandframework.annotations.Argument;
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

@CommandMethod(value = "goto")
@CommandDescription("Warp to the specified coordinates.")
@CommandPermission("voxelsniper.goto")
public class GotoExecutor implements VoxelCommandElement {

    private final VoxelSniperPlugin plugin;

    public GotoExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("<x> <z>")
    public void onGoto(
            final @NotNull Sniper sniper,
            final @Argument("x") int x,
            final @Argument("z") int z
    ) {
        Player player = sniper.getPlayer();

        World world = player.getWorld();
        player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
        sniper.print(Caption.of("voxelsniper.command.goto.woosh"));
    }

}
