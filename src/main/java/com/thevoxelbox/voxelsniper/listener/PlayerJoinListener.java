package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerJoinListener implements Listener<PlayerJoinEvent> {

    private final VoxelSniperPlugin plugin;

    public PlayerJoinListener(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    @Override
    public void listen(PlayerJoinEvent event) {
        VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Sniper sniper = getSniperFromRegistry(uuid);
        if (sniper == null) {
            return;
        }
        if (config.isMessageOnLoginEnabled() && player.hasPermission("voxelsniper.sniper")) {
            sniper.sendInfo(player);
        }
    }

    @Nullable
    private Sniper getSniperFromRegistry(UUID uuid) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Sniper sniper = sniperRegistry.getSniper(uuid);
        return sniper;
    }

}
