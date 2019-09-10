package com.thevoxelbox.voxelsniper.util.painter.setter;

import com.thevoxelbox.voxelsniper.util.painter.BlockSetter;
import net.mcparkour.common.math.vector.Vector3i;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockDataSetter implements BlockSetter {

	private World world;
	private BlockData blockData;
	private boolean applyPhysics;

	public static BlockDataSetterBuilder builder() {
		return new BlockDataSetterBuilder();
	}

	public BlockDataSetter(World world, BlockData blockData, boolean applyPhysics) {
		this.world = world;
		this.blockData = blockData;
		this.applyPhysics = applyPhysics;
	}

	@Override
	public void setBlockAt(Vector3i position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		Block block = this.world.getBlockAt(x, y, z);
		block.setBlockData(this.blockData, this.applyPhysics);
	}

	public World getWorld() {
		return this.world;
	}

	public BlockData getBlockData() {
		return this.blockData;
	}

	public boolean isApplyPhysics() {
		return this.applyPhysics;
	}
}
