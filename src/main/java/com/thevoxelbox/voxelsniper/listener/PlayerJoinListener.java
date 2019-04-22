package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener<PlayerJoinEvent> {

	private VoxelSniperPlugin plugin;

	public PlayerJoinListener(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	@Override
	public void listen(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Sniper sniper = sniperRegistry.getSniper(player);
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		if (player.hasPermission("voxelsniper.sniper") && config.isMessageOnLoginEnabled()) {
			sniper.displayInfo();
		}
	}
}
