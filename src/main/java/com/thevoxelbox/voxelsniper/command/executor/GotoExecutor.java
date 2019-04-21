package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GotoExecutor implements CommandExecutor {

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		try {
			Player player = (Player) sender;
			World world = player.getWorld();
			int x = Integer.parseInt(arguments[0]);
			int z = Integer.parseInt(arguments[1]);
			player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
			sender.sendMessage(ChatColor.GREEN + "Woosh!");
		} catch (NumberFormatException exception) {
			sender.sendMessage(ChatColor.RED + "Invalid syntax.");
		}
	}
}
