package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class VoxelListCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelListCommand(VoxelSniperPlugin plugin) {
		super("VoxelList", "vl", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return true;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return true;
		}
		Message message = snipeData.getMessage();
		if (args.length == 0) {
			RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(sender, sender.getWorld());
			Block targetBlock = rangeBlockHelper.getTargetBlock();
			if (targetBlock == null) {
				return true;
			}
			BlockData blockData = targetBlock.getBlockData();
			snipeData.addToVoxelList(blockData);
			message.voxelList();
			return true;
		} else {
			if (args[0].equalsIgnoreCase("clear")) {
				snipeData.clearVoxelList();
				message.voxelList();
				return true;
			}
		}
		boolean remove = false;
		for (String string : args) {
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
		return true;
	}
}
