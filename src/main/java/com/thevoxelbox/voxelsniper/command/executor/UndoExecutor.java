package com.thevoxelbox.voxelsniper.command.executor;

import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public UndoExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = (Player) sender;
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		if (arguments.length == 1) {
			try {
				int amount = Integer.parseInt(arguments[0]);
				sniper.undo(amount);
			} catch (NumberFormatException exception) {
				sender.sendMessage("Error while parsing amount of undo. Number format exception.");
			}
		} else {
			sniper.undo();
		}
		Logger logger = this.plugin.getLogger();
		logger.info("Player \"" + sender.getName() + "\" used /u");
	}
}
