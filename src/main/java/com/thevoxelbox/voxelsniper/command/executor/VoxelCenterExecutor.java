package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelCenterExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelCenterExecutor(VoxelSniperPlugin plugin) {
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
		Integer center = NumericParser.parseInteger(arguments[0]);
		if (center == null) {
			sender.sendMessage(ChatColor.RED + "Invalid input.");
			return;
		}
		snipeData.setCylinderCenter(center);
		Messages messages = snipeData.getMessages();
		messages.center();
	}
}
