package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerformerExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public PerformerExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		try {
			Brush brush = sniper.getBrush(currentToolId);
			if (brush instanceof PerformerBrush) {
				PerformerBrush performer = (PerformerBrush) brush;
				performer.parse(arguments.length == 0 ? new String[] {"m"} : arguments, snipeData);
			} else {
				sender.sendMessage("This brush is not a performer brush.");
			}
		} catch (RuntimeException exception) {
			exception.printStackTrace();
		}
	}
}
