package com.thevoxelbox.voxelsniper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class SniperRegistry {

	private VoxelSniperPlugin plugin;
	private Map<UUID, Sniper> playerSnipers = new HashMap<>();

	public SniperRegistry(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	public Sniper getSniper(Player player) {
		UUID uuid = player.getUniqueId();
		Sniper sniper = this.playerSnipers.get(uuid);
		if (sniper == null) {
			Sniper newSniper = new Sniper(this.plugin, player);
			this.playerSnipers.put(uuid, newSniper);
			return newSniper;
		}
		return sniper;
	}
}
