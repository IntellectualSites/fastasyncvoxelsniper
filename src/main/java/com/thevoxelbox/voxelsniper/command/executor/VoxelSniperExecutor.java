package com.thevoxelbox.voxelsniper.command.executor;

import java.util.Map;
import java.util.Set;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.performer.Performers;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.util.NumericParser;
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
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		if (arguments.length >= 1) {
			String firstArgument = arguments[0];
			if (firstArgument.equalsIgnoreCase("brushes")) {
				BrushRegistry brushRegistry = this.plugin.getBrushRegistry();
				Map<String, Class<? extends Brush>> brushes = brushRegistry.getBrushes();
				Set<String> handles = brushes.keySet();
				String handlesString = String.join(", ", handles);
				sender.sendMessage(handlesString);
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
				Messages messages = snipeData.getMessages();
				if (arguments.length == 2) {
					Integer range = NumericParser.parseInteger(arguments[1]);
					if (range == null) {
						sender.sendMessage("Can't parse number.");
						return;
					}
					if (range < 0) {
						sender.sendMessage("Negative values are not allowed.");
					}
					snipeData.setRange(range);
					snipeData.setRanged(true);
				} else {
					snipeData.setRanged(!snipeData.isRanged());
				}
				messages.toggleRange();
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
