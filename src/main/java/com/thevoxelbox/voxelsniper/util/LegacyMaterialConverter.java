package com.thevoxelbox.voxelsniper.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.UnsafeValues;

@Deprecated
public final class LegacyMaterialConverter {

	private LegacyMaterialConverter() {
		throw new UnsupportedOperationException("Cannot create instance of utility class");
	}

	public static int getLegacyMaterialId(Material material) {
		UnsafeValues unsafe = Bukkit.getUnsafe();
		Material legacy = unsafe.toLegacy(material);
		return legacy.getId();
	}
}
