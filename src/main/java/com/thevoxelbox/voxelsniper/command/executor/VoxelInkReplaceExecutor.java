package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelInkReplaceExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelInkReplaceExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		BlockData dataValue;
		if (arguments.length == 0) {
			Block targetBlock = player.getTargetBlock(250);
			if (targetBlock != null) {
				dataValue = targetBlock.getBlockData();
			} else {
				return;
			}
		} else {
			try {
				dataValue = Bukkit.createBlockData(arguments[0]);
			} catch (IllegalArgumentException exception) {
				sender.sendMessage("Couldn't parse input.");
				return;
			}
		}
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		snipeData.setReplaceBlockData(dataValue);
		Message message = snipeData.getMessage();
		message.replaceBlockData();
	}
}
