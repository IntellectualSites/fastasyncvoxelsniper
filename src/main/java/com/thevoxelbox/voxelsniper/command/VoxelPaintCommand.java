package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.PaintingWrapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelPaintCommand extends VoxelCommand {

	public VoxelPaintCommand() {
		super("VoxelPaint", "paint", "voxelsniper.sniper");
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("back")) {
				PaintingWrapper.paint(sender, true, true, 0);
				return true;
			} else {
				try {
					PaintingWrapper.paint(sender, false, false, Integer.parseInt(args[0]));
					return true;
				} catch (NumberFormatException exception) {
					sender.sendMessage(ChatColor.RED + "Invalid input.");
					return true;
				}
			}
		} else {
			PaintingWrapper.paint(sender, true, false, 0);
			return true;
		}
	}
}
