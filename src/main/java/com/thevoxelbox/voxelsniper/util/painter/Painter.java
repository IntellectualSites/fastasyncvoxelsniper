package com.thevoxelbox.voxelsniper.util.painter;

import net.mcparkour.common.math.vector.Vector3i;

public interface Painter {

	void paint();

	Vector3i getCenter();

	BlockSetter getBlockSetter();
}
