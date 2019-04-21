package com.thevoxelbox.voxelsniper.util;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * @author MikeMatrix
 */
public class BlockWrapper {

	private BlockData blockData;
	private World world;

	public BlockWrapper(Block block) {
		this.blockData = block.getBlockData();
		this.world = block.getWorld();
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

	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
