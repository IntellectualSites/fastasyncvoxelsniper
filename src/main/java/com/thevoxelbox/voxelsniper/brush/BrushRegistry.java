package com.thevoxelbox.voxelsniper.brush;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Brush registration manager.
 */
public class BrushRegistry {

	private Map<String, Class<? extends Brush>> brushes = new HashMap<>();

	/**
	 * Register a brush for VoxelSniper to be able to use.
	 *
	 * @param brushType Brush implementing Brush interface.
	 * @param handles Handles under which the brush can be accessed ingame.
	 */
	@Deprecated
	public void registerBrush(Class<? extends Brush> brushType, String... handles) {
		for (String handle : handles) {
			registerBrush(handle, brushType);
		}
	}

	public void registerBrush(String handle, Class<? extends Brush> brushType) {
		this.brushes.put(handle, brushType);
	}

	/**
	 * Retrieve Brush class via handle Lookup.
	 *
	 * @param handle Case insensitive brush handle
	 * @return Brush class
	 */
	@Nullable
	public Class<? extends Brush> getBrush(String handle) {
		return this.brushes.get(handle);
	}

	/**
	 * @return Immutable copy of all the registered brushes
	 */
	public Map<String, Class<? extends Brush>> getBrushes() {
		return Map.copyOf(this.brushes);
	}
}
