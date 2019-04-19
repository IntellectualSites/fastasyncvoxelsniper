package com.thevoxelbox.voxelsniper.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.thevoxelbox.voxelsniper.BrushRegistry;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.Performers;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelSniperCommand extends VoxelCommand {

	public VoxelSniperCommand(VoxelSniper plugin) {
		super("VoxelSniper", plugin);
		setIdentifier("vs");
		setPermission("voxelsniper.sniper");
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("brushes")) {
				BrushRegistry brushRegistry = this.plugin.getBrushRegistry();
				List<String> allHandles = new ArrayList<>();
				Map<Class<? extends Brush>, List<String>> brushes = brushRegistry.getBrushes();
				for (List<String> strings : brushes.values()) {
					allHandles.addAll(strings);
				}
				String join = String.join(", ", allHandles);
				player.sendMessage(join);
				return true;
			} else if (args[0].equalsIgnoreCase("range")) {
				SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
				if (args.length == 2) {
					try {
						int range = Integer.parseInt(args[1]);
						if (range < 0) {
							player.sendMessage("Negative values are not allowed.");
						}
						snipeData.setRange(range);
						snipeData.setRanged(true);
						snipeData.getVoxelMessage()
							.toggleRange();
					} catch (NumberFormatException exception) {
						player.sendMessage("Can't parse number.");
					}
				} else {
					snipeData.setRanged(!snipeData.isRanged());
					snipeData.getVoxelMessage()
						.toggleRange();
				}
				return true;
			} else if (args[0].equalsIgnoreCase("perf")) {
				player.sendMessage(ChatColor.AQUA + "Available performers (abbreviated):");
				player.sendMessage(Performers.getPerformerListShort());
				return true;
			} else if (args[0].equalsIgnoreCase("perflong")) {
				player.sendMessage(ChatColor.AQUA + "Available performers:");
				player.sendMessage(Performers.getPerformerListLong());
				return true;
			} else if (args[0].equalsIgnoreCase("enable")) {
				sniper.setEnabled(true);
				player.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return true;
			} else if (args[0].equalsIgnoreCase("disable")) {
				sniper.setEnabled(false);
				player.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return true;
			} else if (args[0].equalsIgnoreCase("toggle")) {
				sniper.setEnabled(!sniper.isEnabled());
				player.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return true;
			}
		}
		player.sendMessage(ChatColor.DARK_RED + "VoxelSniper - Current Brush Settings:");
		sniper.displayInfo();
		return true;
	}
}
