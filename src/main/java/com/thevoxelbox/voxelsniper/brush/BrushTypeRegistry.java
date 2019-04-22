package com.thevoxelbox.voxelsniper.brush;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Brush registration manager.
 */
public class BrushTypeRegistry {

	private Map<String, Class<? extends Brush>> brushTypes = new HashMap<>();

	/**
	 * Register a brush for VoxelSniper to be able to use.
	 *
	 * @param brushType Brush implementing Brush interface.
	 * @param handles Handles under which the brush can be accessed ingame.
	 */
	@Deprecated
	public void registerBrushType(Class<? extends Brush> brushType, String... handles) {
		for (String handle : handles) {
			registerBrushType(handle, brushType);
		}
	}

	public void registerBrushType(String handle, Class<? extends Brush> brushType) {
		this.brushTypes.put(handle, brushType);
	}

	/**
	 * Retrieve Brush class via handle Lookup.
	 *
	 * @param handle Case insensitive brush handle
	 * @return Brush class
	 */
	@Nullable
	public Class<? extends Brush> getBrushType(String handle) {
		return this.brushTypes.get(handle);
	}

	/**
	 * @return Immutable copy of all the registered brushTypes
	 */
	public Map<String, Class<? extends Brush>> getBrushTypes() {
		return Map.copyOf(this.brushTypes);
	}
}
