package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Player player = (Player) sender;
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		String currentToolId = sniper.getCurrentToolId();
		if (currentToolId == null) {
			return;
		}
		SnipeData snipeData = sniper.getSnipeData(currentToolId);
		if (snipeData == null) {
			return;
		}
		Message message = snipeData.getMessage();
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		if (arguments.length == 0) {
			Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
			if (targetBlock != null) {
				if (!sender.hasPermission("voxelsniper.ignorelimitations") && config.getLiteSniperRestrictedItems()
					.contains(targetBlock.getType()
						.getKey()
						.toString())) {
					sender.sendMessage("You are not allowed to use " + targetBlock.getType()
						.name() + ".");
					return;
				}
				snipeData.setBlockDataType(targetBlock.getType());
				message.blockDataType();
			}
			return;
		}
		Material material = Material.matchMaterial(arguments[0]);
		if (material != null && material.isBlock()) {
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && config.getLiteSniperRestrictedItems()
				.contains(material.getKey()
					.toString())) {
				sender.sendMessage("You are not allowed to use " + material.name() + ".");
				return;
			}
			snipeData.setBlockDataType(material);
			message.blockDataType();
		} else {
			sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
		}
	}
}
