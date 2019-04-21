package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
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
		VoxelSniperConfig voxelSniperConfig = this.plugin.getVoxelSniperConfig();
		if (player.hasPermission("voxelsniper.sniper") && voxelSniperConfig.isMessageOnLoginEnabled()) {
			sniper.displayInfo();
		}
	}
}
