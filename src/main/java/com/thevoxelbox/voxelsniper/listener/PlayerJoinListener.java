package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.thevoxelbox.voxelsniper.VoxelSniperPlugin.hasUpdate;
import static com.thevoxelbox.voxelsniper.VoxelSniperPlugin.newVersionTitle;

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
        if (player.hasPermission("voxelsniper.admin") && hasUpdate) {
            player.sendMessage(ChatColor.GOLD + "An update for FastAsyncVoxelSniper is available.");
            player.sendMessage(ChatColor.GOLD + "You are running version " +
                    ChatColor.AQUA + this.plugin.getDescription().getVersion() + ChatColor.GOLD + ", the latest version is " +
                    ChatColor.AQUA + newVersionTitle);
            player.sendMessage(ChatColor.GOLD + "Update at https://dev.bukkit.org/projects/favs");
        }
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
