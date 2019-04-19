package com.thevoxelbox.voxelsniper;

import java.util.Map;
import java.util.UUID;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

/**
 *
 */
public class SniperManager {

	private Map<UUID, Sniper> sniperInstances = Maps.newHashMap();
	private VoxelSniper plugin;

	public SniperManager(VoxelSniper plugin) {
		this.plugin = plugin;
	}

	public Sniper getSniperForPlayer(Player player) {
		if (this.sniperInstances.get(player.getUniqueId()) == null) {
            this.sniperInstances.put(player.getUniqueId(), new Sniper(this.plugin, player));
		}
		return this.sniperInstances.get(player.getUniqueId());
	}
}
