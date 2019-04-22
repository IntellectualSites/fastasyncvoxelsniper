package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.sniper.SnipeAction;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BrushToolExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public BrushToolExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		if (arguments.length > 0) {
			if (arguments[0].equalsIgnoreCase("assign")) {
				SnipeAction action;
				if (arguments[1].equalsIgnoreCase("arrow")) {
					action = SnipeAction.ARROW;
				} else if (arguments[1].equalsIgnoreCase("powder")) {
					action = SnipeAction.GUNPOWDER;
				} else {
					sender.sendMessage("/btool assign <arrow|powder> <toolid>");
					return;
				}
				if (arguments.length == 3 && arguments[2] != null && !arguments[2].isEmpty()) {
					PlayerInventory inventory = player.getInventory();
					ItemStack itemInHand = inventory.getItemInMainHand();
					Material material = itemInHand.getType();
					if (material.isEmpty()) {
						sender.sendMessage("/btool assign <arrow|powder> <toolid>");
						return;
					}
					if (sniper.setTool(arguments[2], action, material)) {
						sender.sendMessage(material.name() + " has been assigned to '" + arguments[2] + "' as action " + action.name() + ".");
					} else {
						sender.sendMessage("Couldn't assign tool.");
					}
					return;
				}
			} else if (arguments[0].equalsIgnoreCase("remove")) {
				if (arguments.length == 2 && arguments[1] != null && !arguments[1].isEmpty()) {
					sniper.removeTool(arguments[1]);
				} else {
					PlayerInventory inventory = player.getInventory();
					ItemStack itemInHand = inventory.getItemInMainHand();
					Material material = itemInHand.getType();
					if (material.isEmpty()) {
						sender.sendMessage("Can't unassign empty hands.");
						return;
					}
					if (sniper.getCurrentToolId() == null) {
						sender.sendMessage("Can't unassign default tool.");
						return;
					}
					sniper.removeTool(sniper.getCurrentToolId(), material);
				}
				return;
			}
		}
		sender.sendMessage("/btool assign <arrow|powder> <toolid>");
		sender.sendMessage("/btool remove [toolid]");
	}
}
