package com.thevoxelbox.voxelsniper.sniper;

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
     * Register the player as a sniper if not already done.
     * Return the sniper directly or after registration.
     *
     * @param player player to register and or get as sniper
     * @return sniper
     */
    public Sniper registerAndGetSniper(Player player) {
        UUID uuid = player.getUniqueId();
        Sniper sniper = getSniper(uuid);
        if (sniper == null) {
            sniper = new Sniper(uuid);
            register(sniper);
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
