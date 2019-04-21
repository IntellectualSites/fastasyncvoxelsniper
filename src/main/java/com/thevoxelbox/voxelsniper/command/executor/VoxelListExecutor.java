package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelListExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelListExecutor(VoxelSniperPlugin plugin) {
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
		Message message = snipeData.getMessage();
		if (arguments.length == 0) {
			RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(player, player.getWorld());
			Block targetBlock = rangeBlockHelper.getTargetBlock();
			if (targetBlock == null) {
				return;
			}
			BlockData blockData = targetBlock.getBlockData();
			snipeData.addToVoxelList(blockData);
			message.voxelList();
			return;
		} else {
			if (arguments[0].equalsIgnoreCase("clear")) {
				snipeData.clearVoxelList();
				message.voxelList();
				return;
			}
		}
		boolean remove = false;
		for (String string : arguments) {
			String materialString;
			if (!string.isEmpty() && string.charAt(0) == '-') {
				remove = true;
				materialString = string.replaceAll("-", "");
			} else {
				materialString = string;
			}
			Material material = Material.matchMaterial(materialString);
			if (material != null && material.isBlock()) {
				BlockData blockData = material.createBlockData();
				if (remove) {
					snipeData.removeFromVoxelList(blockData);
				} else {
					snipeData.addToVoxelList(blockData);
				}
				message.voxelList();
			}
		}
	}
}
