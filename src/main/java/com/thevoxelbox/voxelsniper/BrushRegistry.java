package com.thevoxelbox.voxelsniper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.jetbrains.annotations.Nullable;

/**
 * Brush registration manager.
 */
public class BrushRegistry {

	private Map<Class<? extends Brush>, List<String>> brushes = new HashMap<>();

	/**
	 * Register a brush for VoxelSniper to be able to use.
	 *
	 * @param brushType Brush implementing Brush interface.
	 * @param handles Handles under which the brush can be accessed ingame.
	 */
	public void registerBrush(Class<? extends Brush> brushType, String... handles) {
		List<String> handlesList = Arrays.stream(handles)
			.map(String::toLowerCase)
			.collect(Collectors.toList());
		this.brushes.put(brushType, handlesList);
	}

	/**
	 * Retrieve Brush class via handle Lookup.
	 *
	 * @param handle Case insensitive brush handle
	 * @return Brush class
	 */
	@Nullable
	public Class<? extends Brush> getBrush(String handle) {
		return this.brushes.entrySet()
			.stream()
			.filter(entry -> {
				List<String> handles = entry.getValue();
				return handles.contains(handle);
			})
			.findFirst()
			.map(Entry::getKey)
			.orElse(null);
	}

	/**
	 * @return Amount of Brush classes registered with the system under Sniper visibility.
	 */
	public int getBrushesCount() {
		return this.brushes.size();
	}

	/**
	 * @return Amount of handles registered with the system under Sniper visibility.
	 */
	public int getHandlesCount() {
		return this.brushes.values()
			.stream()
			.mapToInt(List::size)
			.sum();
	}

	/**
	 * @return Immutable copy of all the registered brushes
	 */
	public Map<Class<? extends Brush>, List<String>> getBrushes() {
		return Map.copyOf(this.brushes);
	}
}
