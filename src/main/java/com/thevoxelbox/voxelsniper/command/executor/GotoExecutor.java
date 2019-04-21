package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GotoExecutor implements CommandExecutor {

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		World world = player.getWorld();
		Integer x = NumericParser.parseInteger(arguments[0]);
		Integer z = NumericParser.parseInteger(arguments[1]);
		if (x == null || z == null) {
			sender.sendMessage(ChatColor.RED + "Invalid syntax.");
			return;
		}
		player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
		sender.sendMessage(ChatColor.GREEN + "Woosh!");
	}
}
