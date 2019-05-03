package com.thevoxelbox.voxelsniper.util.painter;

import com.thevoxelbox.voxelsniper.util.math.Vector3i;

public interface Painter {

	void paint();

	Vector3i getCenter();

	BlockSetter getBlockSetter();
}
