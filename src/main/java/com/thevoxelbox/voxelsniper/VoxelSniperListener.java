package com.thevoxelbox.voxelsniper;

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

	private VoxelSniperPlugin plugin;

	public VoxelSniperListener(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
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
