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

public class GotoExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public GotoExecutor(VoxelSniperPlugin plugin) {
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
        int x;
        int z;
        try {
            x = Integer.parseInt(arguments[0]);
            z = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            sniper.print(Caption.of("voxelsniper.command.goto.invalid-syntax", arguments[0], arguments[1]));
            return;
        }
        player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
        sniper.print(Caption.of("voxelsniper.command.goto.woosh"));
    }

}
