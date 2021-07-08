package com.thevoxelbox.voxelsniper.util.material;

import org.bukkit.Material;

public final class Materials {

	private Materials() {
		throw new UnsupportedOperationException("Cannot create instance of this class");
	}

	public static boolean isLiquid(Material material) {
		return material == Material.WATER || material == Material.LAVA;
	}

}
