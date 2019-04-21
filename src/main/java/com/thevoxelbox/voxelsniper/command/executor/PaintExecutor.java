package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.PaintingWrapper;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaintExecutor implements CommandExecutor {

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		if (arguments.length == 1) {
			if (arguments[0].equalsIgnoreCase("back")) {
				PaintingWrapper.paint(player, true, true, 0);
			} else {
				try {
					PaintingWrapper.paint(player, false, false, Integer.parseInt(arguments[0]));
				} catch (NumberFormatException exception) {
					sender.sendMessage(ChatColor.RED + "Invalid input.");
				}
			}
		} else {
			PaintingWrapper.paint(player, true, false, 0);
		}
	}
}
