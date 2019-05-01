package com.thevoxelbox.voxelsniper.command.executor;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.NumericParser;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BrushExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public BrushExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		if (sniper == null) {
			sender.sendMessage(ChatColor.RED + "Sniper not found.");
			return;
		}
		Toolkit toolkit = sniper.getCurrentToolkit();
		if (toolkit == null) {
			sender.sendMessage(ChatColor.RED + "Current toolkit not found.");
			return;
		}
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		if (arguments.length == 0) {
			BrushProperties previousBrushProperties = toolkit.getPreviousBrushProperties();
			if (previousBrushProperties == null) {
				sender.sendMessage(ChatColor.RED + "Previous brush not found.");
				return;
			}
			String permission = previousBrushProperties.getPermission();
			if (permission != null && !player.hasPermission(permission)) {
				sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
				return;
			}
			Brush brush = toolkit.useBrush(previousBrushProperties);
			sniper.sendInfo(sender);
			return;
		}
		String firstArgument = arguments[0];
		Integer brushSize = NumericParser.parseInteger(firstArgument);
		if (brushSize != null) {
			VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
			int litesniperMaxBrushSize = config.getLitesniperMaxBrushSize();
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && brushSize > litesniperMaxBrushSize) {
				brushSize = litesniperMaxBrushSize;
				sender.sendMessage("Size is restricted to " + litesniperMaxBrushSize + " for you.");
			}
			toolkitProperties.setBrushSize(brushSize);
			Messenger messenger = new Messenger(sender);
			messenger.sendBrushSizeMessage(brushSize);
			return;
		}
		BrushRegistry brushRegistry = this.plugin.getBrushRegistry();
		BrushProperties newBrush = brushRegistry.getBrushProperties(firstArgument);
		if (newBrush == null) {
			sender.sendMessage(ChatColor.RED + "Could not find brush for alias " + firstArgument + ".");
			return;
		}
		String permission = newBrush.getPermission();
		if (permission != null && !player.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED + "Insufficient permissions.");
			return;
		}
		Brush brush = toolkit.useBrush(newBrush);
		if (arguments.length > 1) {
			Snipe snipe = new Snipe(sniper, toolkit, toolkitProperties, newBrush, brush);
			if (brush instanceof PerformerBrush) {
				String[] parameters = Arrays.copyOfRange(arguments, 1, arguments.length);
				PerformerBrush performerBrush = (PerformerBrush) brush;
				performerBrush.handleCommand(parameters, snipe);
			} else {
				String[] parameters = hackTheArray(Arrays.copyOfRange(arguments, 1, arguments.length));
				brush.handleCommand(parameters, snipe);
			}
			return;
		}
		sniper.sendInfo(sender);
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
