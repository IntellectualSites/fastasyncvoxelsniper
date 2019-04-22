package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener<PlayerInteractEvent> {

	private VoxelSniperPlugin plugin;

	public PlayerInteractListener(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	@Override
	public void listen(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("voxelsniper.sniper")) {
			return;
		}
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Sniper sniper = sniperRegistry.getSniper(player);
		if (sniper.isEnabled() && sniper.snipe(event.getAction(), event.getMaterial(), event.getClickedBlock(), event.getBlockFace())) {
			event.setCancelled(true);
		}
	}
}
