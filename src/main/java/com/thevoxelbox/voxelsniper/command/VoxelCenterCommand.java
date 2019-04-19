package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelCenterCommand extends VoxelCommand {

	public VoxelCenterCommand(VoxelSniper plugin) {
		super("VoxelCenter", plugin);
		setIdentifier("vc");
		setPermission("voxelsniper.sniper");
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		Sniper sniper = this.plugin.getSniperManager()
			.getSniperForPlayer(player);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		try {
			int center = Integer.parseInt(args[0]);
			snipeData.setcCen(center);
			snipeData.getVoxelMessage()
				.center();
			return true;
		} catch (NumberFormatException exception) {
			player.sendMessage(ChatColor.RED + "Invalid input.");
			return true;
		}
	}
}
