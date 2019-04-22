package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DefaultExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public DefaultExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		sniper.reset(currentToolId);
		sender.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
	}
}
