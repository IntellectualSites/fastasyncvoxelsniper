package com.thevoxelbox.voxelsniper.command.executor;

import com.fastasyncworldedit.core.configuration.Caption;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.util.message.VoxelSniperText;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DefaultExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public DefaultExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.registerAndGetSniper(player);
        if (sniper == null) {
            VoxelSniperText.print(sender, Caption.of("voxelsniper.command.missing-sniper"));
            return;
        }
        Toolkit toolkit = sniper.getCurrentToolkit();
        if (toolkit == null) {
            sniper.print(Caption.of("voxelsniper.command.missing-toolkit"));
            return;
        }
        toolkit.reset();
        sniper.print(Caption.of("voxelsniper.command.default.reset"));
    }

}
