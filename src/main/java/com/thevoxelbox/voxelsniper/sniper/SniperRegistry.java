package com.thevoxelbox.voxelsniper.sniper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class SniperRegistry {

	private int undoCacheSize;
	private Map<UUID, Sniper> snipers = new HashMap<>();

	public SniperRegistry(int undoCacheSize) {
		this.undoCacheSize = undoCacheSize;
	}

	public Sniper getSniper(Player player) {
		UUID uuid = player.getUniqueId();
		Sniper sniper = this.snipers.get(uuid);
		if (sniper == null) {
			Sniper newSniper = new Sniper(player, this.undoCacheSize);
			this.snipers.put(uuid, newSniper);
			return newSniper;
		}
		return sniper;
	}
}
