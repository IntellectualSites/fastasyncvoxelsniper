package com.thevoxelbox.voxelsniper.command.executor;

import java.util.List;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.RangeBlockHelper;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		List<String> liteSniperRestrictedItems = config.getLiteSniperRestrictedItems();
		if (arguments.length == 0) {
			Block targetBlock = new RangeBlockHelper(player, player.getWorld()).getTargetBlock();
			if (targetBlock != null) {
				Material targetBlockType = targetBlock.getType();
				NamespacedKey targetBlockTypeKey = targetBlockType.getKey();
				if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedItems.contains(targetBlockTypeKey.toString())) {
					sender.sendMessage("You are not allowed to use " + targetBlockType.name() + ".");
					return;
				}
				snipeData.setBlockDataType(targetBlockType);
				message.blockDataType();
			}
			return;
		}
		Material material = Material.matchMaterial(arguments[0]);
		if (material != null && material.isBlock()) {
			NamespacedKey key = material.getKey();
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedItems.contains(key.toString())) {
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
