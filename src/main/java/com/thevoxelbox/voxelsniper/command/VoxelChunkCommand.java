package com.thevoxelbox.voxelsniper.command;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class VoxelChunkCommand extends VoxelCommand {

	public VoxelChunkCommand() {
		super("VoxelChunk", "vchunk");
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		World world = sender.getWorld();
		world.refreshChunk(sender.getLocation()
			.getBlockX(), sender.getLocation()
			.getBlockZ());
		return true;
	}
}
