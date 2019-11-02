package com.thevoxelbox.voxelsniper;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.bukkit.BukkitCommand;
import com.boydti.fawe.object.FaweCommand;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public class Favs {
    private final JavaPlugin plugin;

    public Favs(JavaPlugin plugin) {
        this.plugin = plugin;
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
        setupCommand("/p", new FaweCommand("voxelsniper.sniper") {
            @Override
            public boolean execute(Actor fp, String... args) {
                if (fp.isPlayer()) {
                    Player player = BukkitAdapter.adapt((com.sk89q.worldedit.entity.Player) fp);
                    return (Bukkit.getPluginManager().getPlugin("VoxelSniper")).onCommand(player, new Command("p") {
                        @Override
                        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                            return false;
                        }
                    }, null, args);
                }
                return false;
            }
        });
        setupCommand("/d", new FaweCommand("voxelsniper.sniper") {
            @Override
            public boolean execute(Actor fp, String... args) {
                if (fp.isPlayer()) {
                    Player player = BukkitAdapter.adapt((com.sk89q.worldedit.entity.Player) fp);
                    return (Bukkit.getPluginManager().getPlugin("VoxelSniper")).onCommand(player, new Command("d") {
                        @Override
                        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                            return false;
                        }
                    }, null, args);
                }
                return false;
            }
        });
    }

    public void setupCommand(final String label, final FaweCommand cmd) {
        plugin.getCommand(label).setExecutor(new BukkitCommand(cmd));
    }
}
