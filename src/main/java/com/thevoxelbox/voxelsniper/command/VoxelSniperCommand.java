package com.thevoxelbox.voxelsniper.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.thevoxelbox.voxelsniper.BrushRegistry;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.Performers;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VoxelSniperCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelSniperCommand(VoxelSniperPlugin plugin) {
		super("VoxelSniper", "vs", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("brushes")) {
				BrushRegistry brushRegistry = this.plugin.getBrushRegistry();
				List<String> allHandles = new ArrayList<>();
				Map<Class<? extends Brush>, List<String>> brushes = brushRegistry.getBrushes();
				for (List<String> strings : brushes.values()) {
					allHandles.addAll(strings);
				}
				String join = String.join(", ", allHandles);
				sender.sendMessage(join);
				return true;
			} else if (args[0].equalsIgnoreCase("range")) {
				SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
				Message message = snipeData.getMessage();
				if (args.length == 2) {
					try {
						int range = Integer.parseInt(args[1]);
						if (range < 0) {
							sender.sendMessage("Negative values are not allowed.");
						}
						snipeData.setRange(range);
						snipeData.setRanged(true);
						message.toggleRange();
					} catch (NumberFormatException exception) {
						sender.sendMessage("Can't parse number.");
					}
				} else {
					snipeData.setRanged(!snipeData.isRanged());
					message.toggleRange();
				}
				return true;
			} else if (args[0].equalsIgnoreCase("perf")) {
				sender.sendMessage(ChatColor.AQUA + "Available performers (abbreviated):");
				sender.sendMessage(Performers.getPerformerListShort());
				return true;
			} else if (args[0].equalsIgnoreCase("perflong")) {
				sender.sendMessage(ChatColor.AQUA + "Available performers:");
				sender.sendMessage(Performers.getPerformerListLong());
				return true;
			} else if (args[0].equalsIgnoreCase("enable")) {
				sniper.setEnabled(true);
				sender.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return true;
			} else if (args[0].equalsIgnoreCase("disable")) {
				sniper.setEnabled(false);
				sender.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return true;
			} else if (args[0].equalsIgnoreCase("toggle")) {
				sniper.setEnabled(!sniper.isEnabled());
				sender.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return true;
			}
		}
		sender.sendMessage(ChatColor.DARK_RED + "VoxelSniper - Current Brush Settings:");
		sniper.displayInfo();
		return true;
	}
}
