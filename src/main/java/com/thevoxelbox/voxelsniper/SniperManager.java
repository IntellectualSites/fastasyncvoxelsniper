package com.thevoxelbox.voxelsniper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class SniperManager {

	private Map<UUID, Sniper> playerSnipers = new HashMap<>();
	private VoxelSniper plugin;

	public SniperManager(VoxelSniper plugin) {
		this.plugin = plugin;
	}

	public Sniper getSniperForPlayer(Player player) {
		if (this.playerSnipers.get(player.getUniqueId()) == null) {
			this.playerSnipers.put(player.getUniqueId(), new Sniper(this.plugin, player));
		}
		return this.playerSnipers.get(player.getUniqueId());
	}
}
