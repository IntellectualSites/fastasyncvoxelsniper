package com.thevoxelbox.voxelsniper.command.executor;

import java.util.List;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
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
		Messenger messenger = new Messenger(sender);
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		List<Material> liteSniperRestrictedMaterials = config.getLitesniperRestrictedMaterials();
		if (arguments.length == 0) {
			BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
			Block targetBlock = blockTracer.getTargetBlock();
			if (targetBlock != null) {
				Material targetBlockType = targetBlock.getType();
				if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(targetBlockType)) {
					sender.sendMessage("You are not allowed to use " + targetBlockType.name() + ".");
					return;
				}
				toolkitProperties.setBlockType(targetBlockType);
				messenger.sendBlockTypeMessage(targetBlockType);
			}
			return;
		}
		Material material = Material.matchMaterial(arguments[0]);
		if (material != null && material.isBlock()) {
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(material)) {
				sender.sendMessage("You are not allowed to use " + material.name() + ".");
				return;
			}
			toolkitProperties.setBlockType(material);
			messenger.sendBlockTypeMessage(material);
		} else {
			sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
		}
	}
}
