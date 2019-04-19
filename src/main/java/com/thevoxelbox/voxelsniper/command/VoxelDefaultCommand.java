package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelDefaultCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelDefaultCommand(VoxelSniperPlugin plugin) {
		super("VoxelDefault", "d", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		sniper.reset(sniper.getCurrentToolId());
		sender.sendMessage(ChatColor.AQUA + "Brush settings reset to their default values.");
		return true;
	}
}
