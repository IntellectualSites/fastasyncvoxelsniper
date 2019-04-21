package com.thevoxelbox.voxelsniper.command.executor;

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
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelSniperExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelSniperExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = (Player) sender;
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		if (arguments.length >= 1) {
			String firstArgument = arguments[0];
			if (firstArgument.equalsIgnoreCase("brushes")) {
				BrushRegistry brushRegistry = this.plugin.getBrushRegistry();
				List<String> allHandles = new ArrayList<>();
				Map<Class<? extends Brush>, List<String>> brushes = brushRegistry.getBrushes();
				for (List<String> strings : brushes.values()) {
					allHandles.addAll(strings);
				}
				String join = String.join(", ", allHandles);
				sender.sendMessage(join);
				return;
			} else if (firstArgument.equalsIgnoreCase("range")) {
				String currentToolId = sniper.getCurrentToolId();
				if (currentToolId == null) {
					return;
				}
				SnipeData snipeData = sniper.getSnipeData(currentToolId);
				if (snipeData == null) {
					return;
				}
				Message message = snipeData.getMessage();
				if (arguments.length == 2) {
					try {
						int range = Integer.parseInt(arguments[1]);
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
				return;
			} else if (firstArgument.equalsIgnoreCase("perf")) {
				sender.sendMessage(ChatColor.AQUA + "Available performers (abbreviated):");
				sender.sendMessage(Performers.getPerformerListShort());
				return;
			} else if (firstArgument.equalsIgnoreCase("perflong")) {
				sender.sendMessage(ChatColor.AQUA + "Available performers:");
				sender.sendMessage(Performers.getPerformerListLong());
				return;
			} else if (firstArgument.equalsIgnoreCase("enable")) {
				sniper.setEnabled(true);
				sender.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return;
			} else if (firstArgument.equalsIgnoreCase("disable")) {
				sniper.setEnabled(false);
				sender.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return;
			} else if (firstArgument.equalsIgnoreCase("toggle")) {
				sniper.setEnabled(!sniper.isEnabled());
				sender.sendMessage("VoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
				return;
			}
		}
		sender.sendMessage(ChatColor.DARK_RED + "VoxelSniper - Current Brush Settings:");
		sniper.displayInfo();
	}
}
