package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.NumericParser;
import com.thevoxelbox.voxelsniper.util.Painter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaintExecutor implements CommandExecutor {

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		if (arguments.length == 1) {
			if (arguments[0].equalsIgnoreCase("back")) {
				Painter.paint(player, true, true, 0);
			} else {
				Integer choice = NumericParser.parseInteger(arguments[0]);
				if (choice == null) {
					sender.sendMessage(ChatColor.RED + "Invalid input.");
					return;
				}
				Painter.paint(player, false, false, choice);
			}
		} else {
			Painter.paint(player, true, false, 0);
		}
	}
}
