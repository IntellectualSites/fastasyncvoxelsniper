package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VoxelInkCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelInkCommand(VoxelSniperPlugin plugin) {
		super("VoxelInk", "vi", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		byte dataValue;
		if (args.length == 0) {
			Block targetBlock = new RangeBlockHelper(sender, sender.getWorld()).getTargetBlock();
			if (targetBlock != null) {
				dataValue = targetBlock.getData();
			} else {
				return true;
			}
		} else {
			try {
				dataValue = Byte.parseByte(args[0]);
			} catch (NumberFormatException exception) {
				sender.sendMessage("Couldn't parse input.");
				return true;
			}
		}
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		snipeData.setData(dataValue);
		Message message = snipeData.getMessage();
		message.data();
		return true;
	}
}
