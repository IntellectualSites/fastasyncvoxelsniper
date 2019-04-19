package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelDefaultCommand extends VoxelCommand {

	public VoxelDefaultCommand(VoxelSniper plugin) {
		super("VoxelDefault", plugin);
		setIdentifier("d");
		setPermission("voxelsniper.sniper");
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		Sniper sniper = this.plugin.getSniperManager()
			.getSniperForPlayer(player);
		sniper.reset(sniper.getCurrentToolId());
		player.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
		return true;
	}
}
