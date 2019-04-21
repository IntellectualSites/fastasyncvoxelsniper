package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelInkExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelInkExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = (Player) sender;
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		BlockData dataValue;
		if (arguments.length == 0) {
			RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(player, player.getWorld());
			Block targetBlock = rangeBlockHelper.getTargetBlock();
			if (targetBlock != null) {
				dataValue = targetBlock.getBlockData();
			} else {
				return;
			}
		} else {
			try {
				dataValue = Bukkit.createBlockData(arguments[0]);
			} catch (NumberFormatException exception) {
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
		snipeData.setBlockData(dataValue);
		Message message = snipeData.getMessage();
		message.blockData();
	}
}
