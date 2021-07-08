package com.thevoxelbox.voxelsniper.brush.type.stamp;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class StampBrushBlockWrapper {

	private BlockData blockData;
	private int x;
	private int y;
	private int z;

	public StampBrushBlockWrapper(BlockState block, int x, int y, int z) {
		this.blockData = BukkitAdapter.adapt(block);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockData getBlockData() {
		return this.blockData;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return this.z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
