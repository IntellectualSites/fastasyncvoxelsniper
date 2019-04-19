package com.thevoxelbox.voxelsniper.util;

import com.thevoxelbox.voxelsniper.Undo;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class UndoDelegate implements BlockChangeDelegate {

	private World targetWorld;
	private Undo currentUndo;

	public UndoDelegate(World targetWorld) {
		this.targetWorld = targetWorld;
		this.currentUndo = new Undo();
	}

	public Undo getUndo() {
		Undo pastUndo = this.currentUndo;
		this.currentUndo = new Undo();
		return pastUndo;
	}

	public void setBlock(Block block) {
		Location location = block.getLocation();
		Block blockAtLocation = this.targetWorld.getBlockAt(location);
		this.currentUndo.put(blockAtLocation);
		BlockData blockData = block.getBlockData();
		blockAtLocation.setBlockData(blockData);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, @NotNull BlockData blockData) {
		Block block = this.targetWorld.getBlockAt(x, y, z);
		this.currentUndo.put(block);
		block.setBlockData(blockData);
		return true;
	}

	@NotNull
	@Override
	public BlockData getBlockData(int x, int y, int z) {
		Block block = this.targetWorld.getBlockAt(x, y, z);
		return block.getBlockData();
	}

	@Override
	public int getHeight() {
		return this.targetWorld.getMaxHeight();
	}

	@Override
	public boolean isEmpty(int x, int y, int z) {
		Block block = this.targetWorld.getBlockAt(x, y, z);
		return block.isEmpty();
	}
}
