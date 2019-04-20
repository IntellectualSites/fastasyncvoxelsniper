package com.thevoxelbox.voxelsniper.util;

import com.flowpowered.math.vector.Vector3i;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author MikeMatrix
 */
public class BlockWrapper {

	private BlockData blockData;
	private Vector3i position;
	private World world;

	public BlockWrapper(Block block) {
		this.blockData = block.getBlockData();
		Location location = block.getLocation();
		this.position = convertLocationToVector(location);
		this.world = block.getWorld();
	}

	private Vector3i convertLocationToVector(Location location) {
		int blockX = location.getBlockX();
		int blockY = location.getBlockY();
		int blockZ = location.getBlockZ();
		return new Vector3i(blockX, blockY, blockZ);
	}

	public Material getType() {
		return this.blockData.getMaterial();
	}

	public BlockData getBlockData() {
		return this.blockData;
	}

	public void setBlockData(BlockData blockData) {
		this.blockData = blockData;
	}

	public Vector3i getPosition() {
		return this.position;
	}

	public void setPosition(Vector3i position) {
		this.position = position;
	}

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
