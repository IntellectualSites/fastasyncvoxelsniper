package com.thevoxelbox.voxelsniper.util.painter;

import com.thevoxelbox.voxelsniper.util.math.Vector3i;

public final class Painters {

	private Painters() {
		throw new UnsupportedOperationException("Cannot create instance of utility class");
	}

	public static SpherePainter sphere() {
		return new SpherePainter();
	}

	public static BlockPainter block(Painter painter) {
		Vector3i center = painter.getCenter();
		BlockSetter blockSetter = painter.getBlockSetter();
		return new BlockPainter(center, blockSetter);
	}
}
