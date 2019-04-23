package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
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
		if (sniper == null) {
			return;
		}
		Toolkit toolkit = sniper.getCurrentToolkit();
		if (toolkit == null) {
			return;
		}
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		if (toolkitProperties == null) {
			return;
		}
		if (arguments.length == 0) {
			BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
			Block targetBlock = blockTracer.getTargetBlock();
			if (targetBlock != null) {
				toolkitProperties.setReplaceBlockDataType(targetBlock.getType());
				Messages messages = toolkitProperties.getMessages();
				messages.replaceBlockDataType();
			}
			return;
		}
		Material material = Material.matchMaterial(arguments[0]);
		if (material != null) {
			if (material.isBlock()) {
				toolkitProperties.setReplaceBlockDataType(material);
				Messages messages = toolkitProperties.getMessages();
				messages.replaceBlockDataType();
			} else {
				sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
			}
		}
	}
}
