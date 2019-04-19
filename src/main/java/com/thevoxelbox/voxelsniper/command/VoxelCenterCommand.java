package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelCenterCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelCenterCommand(VoxelSniperPlugin plugin) {
		super("VoxelCenter", "vc", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		try {
			int center = Integer.parseInt(args[0]);
			snipeData.setCylinderCenter(center);
			Message message = snipeData.getMessage();
			message.center();
			return true;
		} catch (NumberFormatException exception) {
			sender.sendMessage(ChatColor.RED + "Invalid input.");
			return true;
		}
	}
}
