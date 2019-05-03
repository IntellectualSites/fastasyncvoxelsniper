package com.thevoxelbox.voxelsniper.util.painter;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.util.math.Vector3i;

public class BlockPainter implements Painter {

	private Vector3i center;
	private BlockSetter blockSetter;
	private List<Vector3i> shifts = new ArrayList<>();

	BlockPainter(Vector3i center, BlockSetter blockSetter) {
		this.center = center;
		this.blockSetter = blockSetter;
	}

	public BlockPainter at(int xShift, int yShift, int zShift) {
		Vector3i shift = new Vector3i(xShift, yShift, zShift);
		return at(shift);
	}

	public BlockPainter at(Vector3i shift) {
		this.shifts.add(shift);
		return this;
	}

	@Override
	public void paint() {
		this.shifts.forEach(this::paintBlock);
	}

	private void paintBlock(Vector3i shift) {
		Vector3i position = this.center.add(shift);
		this.blockSetter.setBlockAt(position);
	}

	@Override
	public Vector3i getCenter() {
		return this.center;
	}

	@Override
	public BlockSetter getBlockSetter() {
		return this.blockSetter;
	}
}
