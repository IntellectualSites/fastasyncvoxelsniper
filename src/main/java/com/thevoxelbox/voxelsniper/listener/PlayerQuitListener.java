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

    public PlayerQuitListener(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    @Override
    public void listen(final PlayerQuitEvent event) {
        VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
        if (!config.arePersistentSessionsEnabled()) {
            unregisterSniper(event.getPlayer());
        }
    }

    private void unregisterSniper(Player player) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Sniper sniper = sniperRegistry.getSniper(player);
        if (sniper == null) {
            return;
        }
        sniperRegistry.unregister(sniper);
    }

}
