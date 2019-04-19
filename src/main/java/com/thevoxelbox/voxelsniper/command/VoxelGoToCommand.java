package com.thevoxelbox.voxelsniper.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class VoxelGoToCommand extends VoxelCommand {

	public VoxelGoToCommand() {
		super("VoxelGoTo", "goto", "voxelsniper.sniper");
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		try {
			int x = Integer.parseInt(args[0]);
			int z = Integer.parseInt(args[1]);
			sender.teleport(new Location(sender.getWorld(), x, sender.getWorld()
				.getHighestBlockYAt(x, z), z));
			sender.sendMessage(ChatColor.GREEN + "Woosh!");
			return true;
		} catch (NumberFormatException exception) {
			sender.sendMessage(ChatColor.RED + "Invalid syntax.");
			return true;
		}
	}
}
