package com.thevoxelbox.voxelsniper;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class Favs {
    private final VoxelSniperPlugin plugin;

    public Favs(JavaPlugin plugin) {
        this.plugin = (VoxelSniperPlugin) plugin;
        try {
            this.initFavs();
        } catch (Throwable ignore) {}
    }

    public static void callEvent(Event event) {
        if (Fawe.isMainThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            if (event.isAsynchronous()) {
                Bukkit.getPluginManager().callEvent(event);
            } else {
                try {
                    PluginManager plm = Bukkit.getPluginManager();
                    Class<? extends PluginManager> clazz = plm.getClass();
                    Method methodFireEvent = clazz.getDeclaredMethod("fireEvent", Event.class);
                    methodFireEvent.setAccessible(true);
                    methodFireEvent.invoke(plm, event);
                } catch (Throwable ignore) {}
            }
        }
    }

    public void initFavs() {

        setupCommand("/p", new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
                if (sender instanceof Player && sender.hasPermission("voxelsniper.sniper")) {
                    Player player = (Player) sender;
                    @Nullable PluginCommand cmd = plugin.getCommand("p");
                    plugin.onCommand(sender, cmd, label, args);
                }
                return false;
            }
        });
        setupCommand("/d", new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
                if (sender instanceof Player && sender.hasPermission("voxelsniper.sniper")) {
                    Player player = (Player) sender;
                    @Nullable PluginCommand cmd = plugin.getCommand("d");
                    plugin.onCommand(sender, cmd, label, args);
                }
                return false;
            }
        });
    }

    public void setupCommand(final String label, final CommandExecutor cmd) {
        plugin.getCommand(label).setExecutor(cmd);
    }
}
