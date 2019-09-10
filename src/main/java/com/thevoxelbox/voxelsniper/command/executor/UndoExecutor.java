package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import net.mcparkour.common.text.NumericParser;
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
		if (sniper == null) {
			return;
		}
		if (arguments.length == 1) {
			Integer amount = NumericParser.parseInteger(arguments[0]);
			if (amount == null) {
				sender.sendMessage("Error while parsing amount of undo. Number format exception.");
				return;
			}
			sniper.undo(sender, amount);
			return;
		}
		sniper.undo(sender, 1);
	}
}
