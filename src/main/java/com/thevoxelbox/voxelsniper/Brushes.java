package com.thevoxelbox.voxelsniper;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import org.jetbrains.annotations.Nullable;

/**
 * Brush registration manager.
 */
public class Brushes {

	private Multimap<Class<? extends IBrush>, String> brushes = HashMultimap.create();

	/**
	 * Register a brush for VoxelSniper to be able to use.
	 *
	 * @param clazz Brush implementing IBrush interface.
	 * @param handles Handles under which the brush can be accessed ingame.
	 */
	public void registerSniperBrush(Class<? extends IBrush> clazz, String... handles) {
		Preconditions.checkNotNull(clazz, "Cannot register null as a class.");
		for (String handle : handles) {
			this.brushes.put(clazz, handle.toLowerCase());
		}
	}

	/**
	 * Retrieve Brush class via handle Lookup.
	 *
	 * @param handle Case insensitive brush handle
	 * @return Brush class
	 */
	@Nullable
	public Class<? extends IBrush> getBrushForHandle(String handle) {
		Preconditions.checkNotNull(handle, "Brushhandle can not be null.");
		if (!this.brushes.containsValue(handle.toLowerCase())) {
			return null;
		}
		return this.brushes.entries()
			.stream()
			.filter(entry -> entry.getValue()
				.equalsIgnoreCase(handle))
			.findFirst()
			.map(Entry::getKey)
			.orElse(null);
	}

	/**
	 * @return Amount of IBrush classes registered with the system under Sniper visibility.
	 */
	public int registeredSniperBrushes() {
		return this.brushes.keySet()
			.size();
	}

	/**
	 * @return Amount of handles registered with the system under Sniper visibility.
	 */
	public int registeredSniperBrushHandles() {
		return this.brushes.size();
	}

	/**
	 * @param clazz Brush class
	 * @return All Sniper registered handles for the brush.
	 */
	public Set<String> getSniperBrushHandles(Class<? extends IBrush> clazz) {
		return new HashSet<>(this.brushes.get(clazz));
	}

	/**
	 * @return Immutable Multimap copy of all the registered brushes
	 */
	public Multimap<Class<? extends IBrush>, String> getRegisteredBrushesMultimap() {
		return ImmutableMultimap.copyOf(this.brushes);
	}
}
