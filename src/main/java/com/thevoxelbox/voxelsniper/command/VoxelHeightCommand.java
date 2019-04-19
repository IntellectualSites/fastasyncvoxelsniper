package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelHeightCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelHeightCommand(VoxelSniperPlugin plugin) {
		super("VoxelHeight", "vh", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		try {
			int height = Integer.parseInt(args[0]);
			snipeData.setVoxelHeight(height);
			Message message = snipeData.getMessage();
			message.height();
			return true;
		} catch (NumberFormatException exception) {
			sender.sendMessage(ChatColor.RED + "Invalid input.");
			return true;
		}
	}
}
