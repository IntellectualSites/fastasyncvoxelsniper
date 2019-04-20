package com.thevoxelbox.voxelsniper.command;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.BrushPerformer;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import com.thevoxelbox.voxelsniper.event.SniperBrushSizeChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class VoxelBrushCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelBrushCommand(VoxelSniperPlugin plugin) {
		super("VoxelBrush", "b", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return true;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return true;
		}
		if (args.length == 0) {
			sniper.previousBrush(currentToolId);
			sniper.displayInfo();
			return true;
		}
		PluginManager pluginManager = Bukkit.getPluginManager();
		try {
			int newBrushSize = Integer.parseInt(args[0]);
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && newBrushSize > this.plugin.getVoxelSniperConfig()
				.getLiteSniperMaxBrushSize()) {
				sender.sendMessage("Size is restricted to " + this.plugin.getVoxelSniperConfig()
					.getLiteSniperMaxBrushSize() + " for you.");
				newBrushSize = this.plugin.getVoxelSniperConfig()
					.getLiteSniperMaxBrushSize();
			}
			int originalSize = snipeData.getBrushSize();
			snipeData.setBrushSize(newBrushSize);
			SniperBrushSizeChangedEvent event = new SniperBrushSizeChangedEvent(sniper, originalSize, snipeData.getBrushSize(), currentToolId);
			pluginManager.callEvent(event);
			Message message = snipeData.getMessage();
			message.size();
			return true;
		} catch (NumberFormatException exception) {
			exception.printStackTrace();
		}
		Class<? extends Brush> brush = this.plugin.getBrushRegistry()
			.getBrush(args[0]);
		if (brush != null) {
			Brush originalBrush = sniper.getBrush(currentToolId);
			if (originalBrush == null) {
				return true;
			}
			sniper.setBrush(currentToolId, brush);
			if (args.length > 1) {
				Brush currentBrush = sniper.getBrush(currentToolId);
				if (currentBrush == null) {
					return true;
				}
				if (currentBrush instanceof BrushPerformer) {
					String[] parameters = Arrays.copyOfRange(args, 1, args.length);
					((BrushPerformer) currentBrush).parse(parameters, snipeData);
					return true;
				} else {
					String[] parameters = hackTheArray(Arrays.copyOfRange(args, 1, args.length));
					currentBrush.parameters(parameters, snipeData);
					return true;
				}
			}
			Brush newBrush = sniper.getBrush(currentToolId);
			if (newBrush == null) {
				return true;
			}
			SniperBrushChangedEvent event = new SniperBrushChangedEvent(sniper, originalBrush, newBrush, currentToolId);
			pluginManager.callEvent(event);
			sniper.displayInfo();
		} else {
			sender.sendMessage("Couldn't find Brush for brush handle \"" + args[0] + "\"");
		}
		return true;
	}

	/**
	 * Padds an empty String to the front of the array.
	 *
	 * @param args Array to pad empty string in front of
	 * @return padded array
	 */
	private String[] hackTheArray(String[] args) {
		String[] returnValue = new String[args.length + 1];
		returnValue[0] = "";
		for (int i = 0, argsLength = args.length; i < argsLength; i++) {
			String arg = args[i];
			returnValue[i + 1] = arg;
		}
		return returnValue;
	}
}
