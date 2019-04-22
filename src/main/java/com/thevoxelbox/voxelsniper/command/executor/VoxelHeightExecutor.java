package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.NumericParser;
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
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		Integer height = NumericParser.parseInteger(arguments[0]);
		if (height == null) {
			sender.sendMessage(ChatColor.RED + "Invalid input.");
			return;
		}
		snipeData.setVoxelHeight(height);
		Messages messages = snipeData.getMessages();
		messages.height();
	}
}
