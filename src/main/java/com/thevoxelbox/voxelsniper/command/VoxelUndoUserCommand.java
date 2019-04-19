package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelUndoUserCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelUndoUserCommand(VoxelSniperPlugin plugin) {
		super("VoxelUndoUser", "uu", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(ChatColor.GREEN + "Player not found.");
			return true;
		}
		Sniper sniperForPlayer = sniperManager.getSniperForPlayer(player);
		sniperForPlayer.undo();
		return true;
	}
}
