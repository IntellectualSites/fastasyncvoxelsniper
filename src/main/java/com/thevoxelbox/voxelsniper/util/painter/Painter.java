package com.thevoxelbox.voxelsniper.util.painter;

import com.thevoxelbox.voxelsniper.util.math.vector.Vector3i;

public interface Painter {

	void paint();

	Vector3i getCenter();

	BlockSetter getBlockSetter();
}
