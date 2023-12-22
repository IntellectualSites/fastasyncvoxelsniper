package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener<PlayerQuitEvent> {

    private final VoxelSniperPlugin plugin;
    private final VoxelSniperConfig config;

    public PlayerQuitListener(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getVoxelSniperConfig();
    }

    @EventHandler
    @Override
    public void listen(final PlayerQuitEvent event) {
        unregisterSniper(event.getPlayer());
    }

    private void unregisterSniper(Player player) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Sniper sniper = sniperRegistry.getSniper(player);
        if (sniper == null) {
            return;
        }

        sniper.setPlayer(null);
        if (!config.arePersistentSessionsEnabled()) {
            sniperRegistry.unregister(sniper);
        }
    }

}
