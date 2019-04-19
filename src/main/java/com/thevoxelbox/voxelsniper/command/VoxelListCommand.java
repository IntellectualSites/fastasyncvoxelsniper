package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		if (args.length == 0) {
			RangeBlockHelper rangeBlockHelper = new RangeBlockHelper(sender, sender.getWorld());
			Block targetBlock = rangeBlockHelper.getTargetBlock();
			snipeData.getVoxelList()
				.add(new int[] {targetBlock.getTypeId(), targetBlock.getData()});
			snipeData.getMessage()
				.voxelList();
			return true;
		} else {
			if (args[0].equalsIgnoreCase("clear")) {
				snipeData.getVoxelList()
					.clear();
				snipeData.getMessage()
					.voxelList();
				return true;
			}
		}
		boolean remove = false;
		for (String string : args) {
			String tmpint;
			if (string.startsWith("-")) {
				remove = true;
				tmpint = string.replaceAll("-", "");
			} else {
				tmpint = string;
			}
			try {
				Integer xdat;
				Integer xint;
				if (tmpint.contains(":")) {
					String[] tempintsplit = tmpint.split(":");
					xint = Integer.parseInt(tempintsplit[0]);
					xdat = Integer.parseInt(tempintsplit[1]);
				} else {
					xint = Integer.parseInt(tmpint);
					xdat = -1;
				}
				if (Material.getMaterial(xint) != null && Material.getMaterial(xint)
					.isBlock()) {
					if (!remove) {
						snipeData.getVoxelList()
							.add(new int[] {xint, xdat});
					} else {
						snipeData.getVoxelList()
							.removeValue(new int[] {xint, xdat});
					}
					snipeData.getMessage()
						.voxelList();
				}
			} catch (NumberFormatException ignored) {
			}
		}
		return true;
	}
}
