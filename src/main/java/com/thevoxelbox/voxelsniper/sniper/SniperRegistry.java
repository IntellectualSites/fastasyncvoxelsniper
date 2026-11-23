package com.thevoxelbox.voxelsniper.sniper;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SniperRegistry {

    private final Map<UUID, Sniper> snipers = new HashMap<>();

    /**
     * Register sniper in registry.
     *
     * @param sniper sniper to register
     */
    public void register(Sniper sniper) {
        UUID uuid = sniper.getUuid();
        this.snipers.put(uuid, sniper);
    }

    /**
     * Unregister sniper from registry.
     *
     * @param sniper sniper to unregister
     */
    public void unregister(Sniper sniper) {
        UUID uuid = sniper.getUuid();
        this.snipers.remove(uuid);
    }

    /**
     * Return the associated sniper commander.
     * This method returns the usual sniper and registers, if necessary, for a player.
     *
     * @param commandSender the command sender
     * @return the sniper commander
     * @since 3.0.0
     */
    public SniperCommander getSniperCommander(CommandSender commandSender) {
        return commandSender instanceof Player player
                ? registerAndGetSniper(player)
                : new SniperSender(commandSender);
    }

    /**
     * Register the player as a sniper if not already done.
     * Return the sniper directly or after registration.
     * <p>
     * The player is only stored in the sniper while it is the current online player for its UUID.
     * A player object that is no longer online (for example a command handled after the player quit,
     * or a fake player) is never stored, so the registry does not keep it alive.
     *
     * @param player player to register and or get as sniper
     * @return sniper
     */
    public Sniper registerAndGetSniper(Player player) {
        UUID uuid = player.getUniqueId();
        Sniper sniper = getSniper(uuid);
        if (Bukkit.getPlayer(uuid) != player) {
            // Not the online player for this UUID: do not keep a reference to it.
            return sniper == null ? new Sniper(player) : sniper;
        }
        if (sniper == null) {
            sniper = new Sniper(player);
            register(sniper);
        } else {
            sniper.setPlayer(player);
        }
        return sniper;
    }

    @Nullable
    public Sniper getSniper(Player player) {
        UUID uuid = player.getUniqueId();
        return getSniper(uuid);
    }

    @Nullable
    public Sniper getSniper(UUID uuid) {
        return this.snipers.get(uuid);
    }

    public Map<UUID, Sniper> getSnipers() {
        return Collections.unmodifiableMap(this.snipers);
    }

}
