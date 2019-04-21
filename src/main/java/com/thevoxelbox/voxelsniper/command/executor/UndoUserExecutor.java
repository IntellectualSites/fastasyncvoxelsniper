package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoUserExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public UndoUserExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = Bukkit.getPlayer(arguments[0]);
		if (player == null) {
			sender.sendMessage(ChatColor.GREEN + "Player not found.");
			return;
		}
		Sniper sniperForPlayer = sniperRegistry.getSniper(player);
		sniperForPlayer.undo();
	}
}
