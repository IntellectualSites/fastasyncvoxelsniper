package com.thevoxelbox.voxelsniper.command;

import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.entity.Player;

public class VoxelUndoCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelUndoCommand(VoxelSniperPlugin plugin) {
		super("VoxelUndo", "u", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		if (args.length == 1) {
			try {
				int amount = Integer.parseInt(args[0]);
				sniper.undo(amount);
			} catch (NumberFormatException exception) {
				sender.sendMessage("Error while parsing amount of undo. Number format exception.");
			}
		} else {
			sniper.undo();
		}
		Logger logger = this.plugin.getLogger();
		logger.info("Player \"" + sender.getName() + "\" used /u");
		return true;
	}
}
