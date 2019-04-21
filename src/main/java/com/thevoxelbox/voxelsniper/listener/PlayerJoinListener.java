package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
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
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(player);
		VoxelSniperConfig voxelSniperConfig = this.plugin.getVoxelSniperConfig();
		if (player.hasPermission("voxelsniper.sniper") && voxelSniperConfig.isMessageOnLoginEnabled()) {
			sniper.displayInfo();
		}
	}
}
