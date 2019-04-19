package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Voxel
 */
public class VoxelSniperListener implements Listener {

	private static final String SNIPER_PERMISSION = "voxelsniper.sniper";

	private VoxelSniper plugin;

	public VoxelSniperListener(VoxelSniper plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return boolean Success.
	 */
	public boolean listenCommandExecution(Player player, String command, String[] args) {
		CommandRegistry commandRegistry = this.plugin.getCommandRegistry();
		String commandLowered = command.toLowerCase();
		VoxelCommand foundCommand = commandRegistry.getCommand(commandLowered);
		if (foundCommand == null) {
			return false;
		}
		if (!hasPermission(foundCommand, player)) {
			player.sendMessage(ChatColor.RED + "Insufficient Permissions.");
			return true;
		}
		return foundCommand.onCommand(player, args);
	}

	private boolean hasPermission(VoxelCommand command, Player player) {
		String permission = command.getPermission();
		return permission == null || permission.isEmpty() || player.hasPermission(permission);
	}

	@EventHandler
	public void listenPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission(SNIPER_PERMISSION)) {
			return;
		}
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		if (sniper.isEnabled() && sniper.snipe(event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void listenPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		VoxelSniperConfig voxelSniperConfig = this.plugin.getVoxelSniperConfig();
		if (player.hasPermission(SNIPER_PERMISSION) && voxelSniperConfig.isMessageOnLoginEnabled()) {
			sniper.displayInfo();
		}
	}
}
