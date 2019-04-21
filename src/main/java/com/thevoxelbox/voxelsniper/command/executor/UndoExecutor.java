package com.thevoxelbox.voxelsniper.command.executor;

import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.NumericParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndoExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public UndoExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		if (arguments.length == 1) {
			Integer amount = NumericParser.parseInteger(arguments[0]);
			if (amount == null) {
				sender.sendMessage("Error while parsing amount of undo. Number format exception.");
			} else {
				sniper.undo(amount);
			}
		} else {
			sniper.undo();
		}
		Logger logger = this.plugin.getLogger();
		logger.info("Player \"" + sender.getName() + "\" used /u");
	}
}
