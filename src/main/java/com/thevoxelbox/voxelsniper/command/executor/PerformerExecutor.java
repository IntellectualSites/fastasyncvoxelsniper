package com.thevoxelbox.voxelsniper.command.executor;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.BrushPerformer;
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
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = (Player) sender;
		Sniper sniper = sniperManager.getSniperForPlayer(player);
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
			if (arguments == null || arguments.length == 0) {
				if (brush instanceof BrushPerformer) {
					((BrushPerformer) brush).parse(new String[] {"m"}, snipeData);
				} else {
					sender.sendMessage("This brush is not a performer brush.");
				}
			} else {
				if (brush instanceof BrushPerformer) {
					((BrushPerformer) brush).parse(arguments, snipeData);
				} else {
					sender.sendMessage("This brush is not a performer brush.");
				}
			}
		} catch (NumberFormatException exception) {
			Logger logger = this.plugin.getLogger();
			logger.log(Level.WARNING, "Command error from " + sender.getName(), exception);
		}
	}
}
