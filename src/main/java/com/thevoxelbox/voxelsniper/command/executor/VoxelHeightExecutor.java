package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelHeightExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelHeightExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = (Player) sender;
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		try {
			int height = Integer.parseInt(arguments[0]);
			snipeData.setVoxelHeight(height);
			Message message = snipeData.getMessage();
			message.height();
		} catch (NumberFormatException exception) {
			sender.sendMessage(ChatColor.RED + "Invalid input.");
		}
	}
}
