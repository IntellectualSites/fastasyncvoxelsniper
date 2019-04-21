package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelReplaceExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelReplaceExecutor(VoxelSniperPlugin plugin) {
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
		if (arguments.length == 0) {
			Block targetBlock = player.getTargetBlock(250);
			if (targetBlock != null) {
				snipeData.setReplaceBlockDataType(targetBlock.getType());
				Message message = snipeData.getMessage();
				message.replaceBlockDataType();
			}
			return;
		}
		Material material = Material.matchMaterial(arguments[0]);
		if (material != null) {
			if (material.isBlock()) {
				snipeData.setReplaceBlockDataType(material);
				Message message = snipeData.getMessage();
				message.replaceBlockDataType();
			} else {
				sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
			}
		}
	}
}
