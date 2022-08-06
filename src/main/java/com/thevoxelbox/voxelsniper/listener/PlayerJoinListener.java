package com.thevoxelbox.voxelsniper.listener;

import com.fastasyncworldedit.core.configuration.Caption;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.thevoxelbox.voxelsniper.VoxelSniperPlugin.hasUpdate;
import static com.thevoxelbox.voxelsniper.VoxelSniperPlugin.newVersionTitle;
import static com.thevoxelbox.voxelsniper.VoxelSniperPlugin.updateCheckFailed;

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
        Sniper sniper = getSniperFromRegistry(player);
        if (player.hasPermission("voxelsniper.admin") && (hasUpdate || updateCheckFailed) && config.areUpdateNotificationsEnabled()) {
            if (updateCheckFailed) {
                sniper.print(Caption.of("favs.info.update.check-failed"));
            } else {
                sniper.print(Caption.of(
                        "favs.info.update.update-available",
                        this.plugin.getDescription().getVersion(),
                        newVersionTitle,
                        TextComponent
                                .of("https://dev.bukkit.org/projects/favs")
                                .clickEvent(ClickEvent.openUrl("https://dev.bukkit.org/projects/favs"))
                ));
            }
        }
        if (config.isMessageOnLoginEnabled() && player.hasPermission("voxelsniper.sniper")) {
            sniper.sendInfo(player, true);
        }
    }

    private Sniper getSniperFromRegistry(Player player) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        return sniperRegistry.registerAndGetSniper(player);
    }

}
