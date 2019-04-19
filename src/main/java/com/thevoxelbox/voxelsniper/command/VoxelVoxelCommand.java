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

public class VoxelVoxelCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelVoxelCommand(VoxelSniperPlugin plugin) {
		super("VoxelVoxel", "v", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		Message message = snipeData.getMessage();
		if (args.length == 0) {
			Block targetBlock = new RangeBlockHelper(sender, sender.getWorld()).getTargetBlock();
			if (targetBlock != null) {
				if (!sender.hasPermission("voxelsniper.ignorelimitations") && this.plugin.getVoxelSniperConfig()
					.getLiteSniperRestrictedItems()
					.contains(targetBlock.getTypeId())) {
					sender.sendMessage("You are not allowed to use " + targetBlock.getType()
						.name() + ".");
					return true;
				}
				snipeData.setVoxelId(targetBlock.getTypeId());
				message.voxel();
			}
			return true;
		}
		Material material = Material.matchMaterial(args[0]);
		if (material != null && material.isBlock()) {
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && this.plugin.getVoxelSniperConfig()
				.getLiteSniperRestrictedItems()
				.contains(material.getId())) {
				sender.sendMessage("You are not allowed to use " + material.name() + ".");
				return true;
			}
			snipeData.setVoxelId(material.getId());
			message.voxel();
		} else {
			sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
		}
		return true;
	}
}
