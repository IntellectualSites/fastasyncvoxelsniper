package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VoxelReplaceCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelReplaceCommand(VoxelSniperPlugin plugin) {
		super("VoxelReplace", "vr", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		if (args.length == 0) {
			Block targetBlock = new RangeBlockHelper(sender, sender.getWorld()).getTargetBlock();
			if (targetBlock != null) {
				snipeData.setReplaceBlockDataType(targetBlock.getType());
				Message message = snipeData.getMessage();
				message.replaceBlockDataType();
			}
			return true;
		}
		Material material = Material.matchMaterial(args[0]);
		if (material != null) {
			if (material.isBlock()) {
				snipeData.setReplaceBlockDataType(material);
				Message message = snipeData.getMessage();
				message.replaceBlockDataType();
			} else {
				sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
			}
			return true;
		}
		return false;
	}
}
